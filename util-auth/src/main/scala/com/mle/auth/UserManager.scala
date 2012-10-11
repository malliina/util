package com.mle.auth

/**
 * @author Mle
 */
/**
 *
 */
trait UserManager {
  /**
   *
   * @param user
   * @throws Exception if that user already exists
   */
  def addUser(user: String, password: String)

  /**
   *
   * @param user
   * @throws Exception if the user does not exist
   */
  def removeUser(user: String)

  /**
   *
   * @param user
   * @param newPassword
   * @throws Exception if the user doesn't exist or if the new password defies any policy
   */
  def setPassword(user: String, newPassword: String)

  def addGroup(group: String)

  def removeGroup(group: String)

  /**
   *
   * @param user
   * @param group
   * @throws Exception if the user already exists in the group
   * @throws Exception if the group or user does not exist
   */
  def assign(user: String, group: String)

  /**
   *
   * @param user
   * @param group
   * @throws Exception if the user does not exist in the group
   * @throws Exception if the group or user does not exist
   */
  def revoke(user: String, group: String)

  /**
   *
   * @param user
   * @param group
   * @return
   * @throws Exception if the user or group does not exist
   */
  def belongs(user: String, group: String): Boolean

  /**
   *
   * @param user
   * @return
   * @throws Exception if the user does not exist
   */
  def groups(user: String): Seq[String]

  /**
   *
   * @param group
   * @return
   * @throws Exception if the group does not exist
   */
  def users(group: String): Seq[String]

  def users: Seq[String]
}
