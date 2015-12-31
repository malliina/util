package com.malliina.auth

/**
 * Not used because I want to mix multiple authenticator traits into the same class
 * in order to support multiple authentication methods. If they all extend one trait, I get
 * "inherits different type instances of trait Authenticator" ...
 *
 * @author mle
 */
/**
 *
 * @tparam T type of credential: a user-pass container, a certificate chain, ...
 * @tparam U type of user identifier
 */
trait Authenticator[T, U] {
  /**
   * Authenticates a user using the given credentials.
   *
   * If this method returns normally, the authentication was successful.
   *
   * @param credential user/pass, certificate chain, ...
   * @return the user object
   * @throws Exception if authentication fails
   */
  def authenticate(credential: T): U
}
