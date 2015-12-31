package com.malliina.auth

import java.security.cert.X509Certificate

/**
 * Authenticates the user based on a supplied certificate chain.
 *
 * This class does not verify that the client certificate is signed by a ca certificate the server trusts.
 * It is assumed that this step has already been done,
 * and in web contexts it is already done by the web container by the time this trait is used, for example by Jetty.
 *
 * Therefore to "authenticate", implementations only need to read the CN from the DN
 * and check if such a user exists in the user database.
 * @tparam T type of user identifier
 * @author mle
 */
trait CertificateAuthenticator[T] extends Authenticator[Seq[X509Certificate], T] {
  def authenticate(certChain: Seq[X509Certificate]): T
}
