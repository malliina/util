package com.mle.auth

/**
 *
 * @author mle
 */
/**
 *
 * @tparam T type of credential: a user-pass container, a certificate chain, ...
 * @tparam U
 */
trait Authenticator[T, U] {
  def authenticate(credential: T): U
}
