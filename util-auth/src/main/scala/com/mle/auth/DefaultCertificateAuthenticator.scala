package com.mle.auth

import exception.AuthException
import java.security.cert.X509Certificate
import com.mle.util.Log

/**
 * The "self" keyword says this trait must be mixed in with a UserManager
 *
 * @author mle
 */
trait DefaultCertificateAuthenticator extends CertificateAuthenticator[String] with Log {
  // need usermanager to check if the user denoted by the CN exists
  self: UserManager =>
  def authenticate(certChain: Seq[X509Certificate]) = {
    val certInfo = new CertificateContainer(certChain)
    val cn = certInfo.cn
    if (this existsUser cn) {
      log info "Authenticated with CN: " + cn
      cn
    }
    else {
      log info "Authentication failed for CN: " + cn
      throw new AuthException("User with CN: " + cn + " does not exist")
    }
  }
}
