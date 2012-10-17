package com.mle.jdbc.auth

import com.mle.auth.{Authenticator, UserManager}
import com.mle.jdbc.schema.DbTable
import com.mle.jdbc.DB
import com.mle.auth.exception.AuthException

/**
 *
 * @author mle
 */
class JDBCUserManager(connProvider: SQLConnectionProvider) extends UserManager with Authenticator[String] {

  object Users extends DbTable("users")

  object Groups extends DbTable("groups")

  object UserGroup extends DbTable("usergroup")

  def authenticate(user: String, password: String) = {
    Users.select("name")("name" -> user, "password" -> password)(_ getString 1).headOption
      .getOrElse(throw new AuthException("Authentication failed"))
  }

  /**
   *
   * @param user
   * @throws Exception if that user already exists
   */
  def addUser(user: String, password: String) {
    Users.insert("name" -> user, "password" -> password)
  }

  /**
   *
   * @param user
   * @throws Exception if the user does not exist
   */
  def removeUser(user: String) {
    Users delete "name" -> user
  }

  /**
   *
   * @param user
   * @param newPassword
   * @throws Exception if the user doesn't exist or if the new password defies any policy
   */
  def setPassword(user: String, newPassword: String) {
    Users.update("password" -> newPassword)(where = "name" -> user)
  }

  def addGroup(group: String) {
    Groups insert "name" -> group
  }

  def removeGroup(group: String) {
    Groups delete "name" -> group
  }

  /**
   *
   * @param user
   * @param group
   * @throws Exception if the user already exists in the group
   * @throws Exception if the group or user does not exist
   */
  def assign(user: String, group: String) {
    val userId = Users id "name" -> user
    val groupId = Groups id "name" -> group
    UserGroup insert("user_id" -> userId, "group_id" -> groupId)
  }

  def groups = DB.query("select name from " + Groups)(_ getString 1)

  /**
   *
   * @param user
   * @param group
   * @throws Exception if the user does not exist in the group
   * @throws Exception if the group or user does not exist
   */
  def revoke(user: String, group: String) {
    val userId = Users id "name" -> user
    val groupId = Groups id "name" -> group
    UserGroup delete("user_id" -> userId, "group_id" -> groupId)
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
  def groups(user: String) = DB.query("select name from " + Groups + " where id=" +
    "(select group_id from " + UserGroup + " where user_id=" +
    "(select id from " + Users + " where name=?))", user)(_ getString 1)

  /**
   *
   * @param group
   * @return
   * @throws Exception if the group does not exist
   */
  def users(group: String) = DB.query("select name from " + Users + " where id=" +
    "(select user_id from " + UserGroup + " where group_id=" +
    "(select id from " + Groups + " where name=?))", group)(_ getString 1)

  def users = DB.query("select name from " + Users)(_ getString 1)
}
