package com.mle.auth

/**
 * @author Mle
 */
trait Authenticator[T] {
  /**
   *
   * @param user
   * @param password
   * @throws Exception if authentication fails
   */
  def authenticate(user: String, password: String): T
}
