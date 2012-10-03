package com.mle.auth.ldap

import com.mle.auth.ConnectionProvider
import javax.naming.directory.InitialDirContext

/**
 * TODO: Connection pooling
 *
 * @author Mle
 */
trait LDAPConnectionProvider extends ConnectionProvider[InitialDirContext] {
  def user: String

  def password: String

  def authenticator: LDAPAuthenticator

  /**
   *
   * @return a new connection object
   */
  def connection = authenticator.authenticate(user, password)
}
