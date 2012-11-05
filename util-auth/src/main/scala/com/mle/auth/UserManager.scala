package com.mle.auth

/**
 * @author Mle
 */
/**
 * @tparam T type of user
 */
trait UserManager[T] {
  /**
   *
   * @param user
   * @throws Exception if that user already exists
   */
  def addUser(user: T, password: String)

  /**
   *
   * @param user
   * @throws Exception if the user does not exist
   */
  def removeUser(user: T)

  /**
   *
   * @param user
   * @param newPassword
   * @throws Exception if the user doesn't exist or if the new password defies any policy
   */
  def setPassword(user: T, newPassword: String)

  def addGroup(group: String)

  def removeGroup(group: String)

  /**
   *
   * @param user
   * @param group
   * @throws Exception if the user already exists in the group
   * @throws Exception if the group or user does not exist
   */
  def assign(user: T, group: String)

  /**
   *
   * @param user
   * @param group
   * @throws Exception if the user does not exist in the group
   * @throws Exception if the group or user does not exist
   */
  def revoke(user: T, group: String)

  /**
   *
   * @param user
   * @param group
   * @return
   * @throws Exception if the user or group does not exist
   */
  def belongs(user: T, group: String): Boolean

  /**
   *
   * @param user
   * @return
   * @throws Exception if the user does not exist
   */
  def groups(user: T): Seq[String]

  /**
   *
   * @param group
   * @return
   * @throws Exception if the group does not exist
   */
  def users(group: String): Seq[String]

  def users: Seq[T]

  def groups: Seq[String]

  def existsUser(user: T) = users contains user

  // Move?

  /**
   * @param user user id
   * @return a record of the given user: group membership etc.
   */
  def userInfo(user: T): User = User(user, groups(user))

  /**
   * Implementations should optimize this call.
   *
   * @return the user database
   */
  def userDatabase: Seq[User] = users map userInfo

  case class User(userId: T, groups: Seq[String] = Seq.empty, password: Option[String] = None)

}
