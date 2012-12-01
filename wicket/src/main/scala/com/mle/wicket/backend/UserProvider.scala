package com.mle.wicket.backend

import com.mle.auth.UserManager
import com.mle.wicket.markup.AbstractUsers.User

/**
 *
 * @author mle
 */
trait UserProvider extends UserManager[String] {
  /**
   * @param user user id
   * @return a record of the given user: group membership etc.
   */
  def userInfo(user: String): User =User(user, groups(user))

  /**
   * Implementations should optimize this call.
   *
   * @return the user database
   */
  def userDatabase: Seq[User] = users map userInfo
}
