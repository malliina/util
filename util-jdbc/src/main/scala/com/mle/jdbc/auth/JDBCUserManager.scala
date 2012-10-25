package com.mle.jdbc.auth

import com.mle.auth.{Authenticator, UserManager}
import com.mle.jdbc.schema.UserMgmtSchema
import com.mle.jdbc.DB
import com.mle.auth.exception.AuthException

/**
 *
 * @author mle
 */
abstract class JDBCUserManager(schema: UserMgmtSchema)
  extends UserManager with Authenticator[String] {
  val usersTable = schema.usersTable
  val usernameCol = "name"
  val passwordCol = "password"
  val groupsTable = schema.groupsTable
  val groupnameCol = "name"
  val userGroup = schema.userGroup

  def authenticate(user: String, password: String) = {
    usersTable.select(usernameCol)(usernameCol -> user, passwordCol -> password)(_ getString 1).headOption
      .getOrElse(throw new AuthException("Authentication failed"))
  }

  override def addUser(user: String, password: String) {
    usersTable.insert(usernameCol -> user, passwordCol -> password)
  }

  def removeUser(user: String) {
    usersTable delete usernameCol -> user
  }

  def setPassword(user: String, newPassword: String) {
    usersTable.update(passwordCol -> newPassword)(where = usernameCol -> user)
  }

  def addGroup(group: String) {
    groupsTable insert groupnameCol -> group
  }

  def removeGroup(group: String) {
    groupsTable delete groupnameCol -> group
  }

  /**
   *
   * @param user
   * @param group
   * @throws Exception if the user already exists in the group
   * @throws Exception if the group or user does not exist
   */
  def assign(user: String, group: String) {
    val userId = usersTable id usernameCol -> user
    val groupId = groupsTable id groupnameCol -> group
    userGroup insert("user_id" -> userId, "group_id" -> groupId)
  }

  def groups = DB.query("select " + groupnameCol + " from " + groupsTable)(_ getString 1)

  /**
   *
   * @param user
   * @param group
   * @throws Exception if the user does not exist in the group
   * @throws Exception if the group or user does not exist
   */
  def revoke(user: String, group: String) {
    val userId = usersTable id usernameCol -> user
    val groupId = groupsTable id groupnameCol -> group
    userGroup delete("user_id" -> userId, "group_id" -> groupId)
  }

  /**
   *
   * @param user
   * @param group
   * @return
   * @throws Exception if the user or group does not exist
   */
  def belongs(user: String, group: String) = false

  /**
   *
   * @param user
   * @return
   * @throws Exception if the user does not exist
   */
  def groups(user: String) = DB.query("select " + groupnameCol + " from " + groupsTable + " where id=" +
    "(select group_id from " + userGroup + " where user_id=" +
    "(select id from " + usersTable + " where " + usernameCol + "=?))", user)(_ getString 1)

  /**
   *
   * @param group
   * @return
   * @throws Exception if the group does not exist
   */
  def users(group: String) = DB.query("select " + usernameCol + " from " + usersTable + " where id=" +
    "(select user_id from " + userGroup + " where group_id=" +
    "(select id from " + groupsTable + " where " + groupnameCol + "=?))", group)(_ getString 1)

  def users = DB.query("select " + usernameCol + " from " + usersTable)(_ getString 1)
}
