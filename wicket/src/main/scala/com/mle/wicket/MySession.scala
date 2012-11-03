package com.mle.wicket

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession
import org.apache.wicket.request.Request
import org.apache.wicket.authroles.authorization.strategies.role.Roles
import java.security.cert.X509Certificate
import com.mle.db.DatabaseSettings
import com.mle.util.Util._
import org.apache.wicket.Session

/**
 * Session that supports authentication using username/password credentials or client certificates.
 *
 * @author mle
 */
class MySession(req: Request) extends AuthenticatedWebSession(req) {
  def authenticator = DatabaseSettings.userManager

  /**
   * TODO: Read roles from user management service.
   * @return the default admin role
   */
  def getRoles = new Roles(Roles.ADMIN)

  /**
   * Don't call me, call signIn(user,pass)
   * @param username
   * @param password
   * @return
   */
  def authenticate(username: String, password: String) = validate(authenticator.authenticate(username, password))

  /**
   * Don't call me, call signIn(certChain)
   * @param certChain
   * @return
   */
  def authenticate(certChain: Seq[X509Certificate]): Boolean = validate(authenticator.authenticate(certChain))

  /**
   *
   * @return true if the evaluation of the given authFunction does not throw an exception, false if it does
   */
  private def validate[T](authFunction: => T) = optionally(authFunction)
    .map(_ => true).getOrElse(false)

  def signIn(certChain: Seq[X509Certificate]): Boolean = {
    signIn(authenticate(certChain))
    if (isSignedIn) {
      bind()
    }
    isSignedIn
  }
}

object MySession {
  def get() = Session.get().asInstanceOf[MySession]
}
