package com.malliina.auth

import exception.AuthException
import java.security.cert.X509Certificate
import com.malliina.util.Log

/**
 * The "self" keyword says this trait must be mixed in with a UserManager
 *
 * @author mle
 */
trait DefaultCertificateAuthenticator extends CertificateAuthenticator[String] with Log {
  // need usermanager to check if the user denoted by the CN exists
  self: UserManager[String] =>
  def authenticate(certChain: Seq[X509Certificate]) = {
    val certInfo = new CertificateContainer(certChain)
    val cn = certInfo.cn
    if (this.existsUser(cn)) {
      log info "Authentication successful for CN: " + cn
      cn
    } else {
      log info "Authentication failed for CN: " + cn
      throw new AuthException("User with CN: " + cn + " does not exist")
    }
    //    val user = this.userInfo(cn).getOrElse {
    //      log info "Authentication failed for CN: " + cn
    //      throw new AuthException("User with CN: " + cn + " does not exist")
    //    }
    //    log info "Authentication successful for CN: " + cn
    //    user
  }
}
