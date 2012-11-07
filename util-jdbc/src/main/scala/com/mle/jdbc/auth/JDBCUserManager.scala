package com.mle.jdbc.auth

import com.mle.auth._
import com.mle.auth.exception.AuthException
import com.mle.jdbc.schema.UserMgmtSchema

/**
 *
 * @author mle
 */
abstract class JDBCUserManager(schema: UserMgmtSchema)
  extends UserManager[String]
  with PasswordAuthenticator[String]
  with DefaultCertificateAuthenticator {
  val usersTable = schema.usersTable
  val usernameCol = usersTable.name.name
  val passwordCol = usersTable.password.name
  val groupsTable = schema.groupsTable
  val groupnameCol = groupsTable.name.name
  val userGroup = schema.userGroup
  val userIdCol = userGroup.user_id.name
  val groupIdCol = userGroup.group_id.name

  def authenticate(user: String, password: String) = {
    usersTable.select(usernameCol)(usernameCol -> user, passwordCol -> password)(_ getString 1).headOption
      .getOrElse(throw new AuthException("Password authentication failed for user: " + user))
  }

  /**
   * A performance optimization to reading all users which is the default impl.
   *
   * @param user the user to look for
   * @return true if the user exists, false otherwise
   */
  override def existsUser(user: String) = usersTable.select(usernameCol)(usernameCol -> user)(_ getString 1)
    .headOption.map(_ => true).getOrElse(false)

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
    userGroup insert(userIdCol -> userId, groupIdCol -> groupId)
  }

  def groups = usersTable.db.query("select " + groupnameCol + " from " + groupsTable)(_ getString 1)

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
    userGroup delete(userIdCol -> userId, groupIdCol -> groupId)
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
  def groups(user: String) = groupsTable.db.query("select " + groupnameCol + " from " + groupsTable + " " +
    "where " + groupsTable.id + " IN " +
    "(select " + groupIdCol + " from " + userGroup + " where " + userIdCol + "=" +
    "(select " + usersTable.id + " from " + usersTable + " where " + usernameCol + "=?))", user)(_ getString 1)

  /**
   *
   * @param group
   * @return
   * @throws Exception if the group does not exist
   */
  def users(group: String) = usersTable.db.query("select " + usernameCol + " from " + usersTable + " " +
    "where " + usersTable.id + " IN " +
    "(select " + userIdCol + " from " + userGroup + " where " + groupIdCol + "=" +
    "(select " + groupsTable.id + " from " + groupsTable + " where " + groupnameCol + "=?))", group)(_ getString 1)

  def users = usersTable.db.query("select " + usernameCol + " from " + usersTable)(_ getString 1)

}
