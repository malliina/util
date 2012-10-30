package com.mle.auth

/**
 *
 * @author mle
 */
/**
 *
 * @tparam T type of credential: a user-pass container, a certificate chain, ...
 * @tparam U type of user
 */
trait Authenticator[T, U] {
  def authenticate(credential: T): U
}
