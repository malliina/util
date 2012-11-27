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
   * Removes the user including any attributes like group membership.
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

  def addGroups(groups: String*) {
    groups foreach addGroup
  }

  /**
   *
   * @param group the group to remove
   * @throws UserManagementException if the group is not empty
   */
  def removeGroup(group: String)

  def removeGroups(groups: String*) {
    groups foreach removeGroup
  }

  /**
   *
   * @param user
   * @param group
   * @throws Exception if the user already exists in the group
   * @throws Exception if the group or user does not exist
   */
  def assign(user: T, group: String)

  def assign(user: T, groups: Seq[String]) {
    groups foreach (group => assign(user, group))
  }

  /**
   *
   * @param user
   * @param group
   * @throws Exception if the user does not exist in the group
   * @throws Exception if the group or user does not exist
   */
  def revoke(user: T, group: String)

  def revoke(user: T, groups: Seq[String]) {
    groups foreach (group => revoke(user, group))
  }

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

  /**
   * Sets the group membership for the given user.
   *
   * Any pre-existing group membership not included in the supplied groups is revoked.
   *
   * @param user
   * @param newGroups
   */
  def groups(user: T, newGroups: Seq[String]) {
    val oldGroups = groups(user)
    val removeGroups = oldGroups filterNot newGroups.contains
    val addGroups = newGroups filterNot oldGroups.contains
    removeGroups foreach (g => revoke(user, g))
    addGroups foreach (g => assign(user, g))
  }

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
