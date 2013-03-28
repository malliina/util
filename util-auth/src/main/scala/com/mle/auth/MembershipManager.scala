package com.mle.auth

/**
 * 
 * @author mle
 */
trait MembershipManager[T] {
  /**
   *
   * @param user
   * @param group
   * @throws Exception if the group or user does not exist or if the user already exists in the group
   */
  def assign(user: T, group: String)

  def assign(user: T, groups: Seq[String]) {
    groups foreach (group => assign(user, group))
  }

  /**
   *
   * @param user
   * @param group
   * @throws Exception if the group or user does not exist or if the user does not exist in the group
   */
  def revoke(user: T, group: String)

  def revoke(user: T, groups: Seq[String]) {
    groups foreach (group => revoke(user, group))
  }
//  /**
//   *
//   * @param user
//   * @param group
//   * @return
//   * @throws Exception if the user or group does not exist
//   */
////  def belongs(user: T, group: String): Boolean
}
