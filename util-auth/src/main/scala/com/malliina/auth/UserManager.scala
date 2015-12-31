package com.malliina.auth

import com.malliina.auth.exception.UserManagementException

/**
 * @author Mle
 */
/**
 * @tparam T type of user
 */
trait UserManager[T] extends MembershipManager[T] {
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

  def users(group: String): Seq[T]

  def groups(user: T): Seq[String]

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
  def replaceGroups(user: T, newGroups: Seq[String]) {
    val oldGroups = groups(user)
    val removeGroups = oldGroups filterNot newGroups.contains
    val addGroups = newGroups filterNot oldGroups.contains
    removeGroups foreach (g => revoke(user, g))
    addGroups foreach (g => assign(user, g))
  }
}
