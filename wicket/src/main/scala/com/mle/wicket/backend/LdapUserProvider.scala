package com.mle.wicket.backend

import com.mle.auth.ldap.AbstractLdapUserManager
import com.mle.wicket.markup.AbstractUsers.User

/**
 *
 * @author mle
 */
trait LdapUserProvider extends UserProvider {
  def hostManager: AbstractLdapUserManager

  /**
   * @param user user id
   * @return a record of the given user: group membership etc.
   */
  override def userInfo(user: String) = User(user, groups(user), hostManager.groups(user))
}
