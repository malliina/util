package com.mle.auth

import crypto.Hashing

/**
 * This is bullshit and should be part of trait [[com.mle.auth.UserManager]]
 *
 * @author Mle
 */
trait Authenticator[T] {
  /**
   * If this method returns normally, the authentication was successful.
   *
   * @param user
   * @param password
   * @throws Exception if authentication fails
   */
  def authenticate(user: String, password: String): T
}

trait HashingAuthenticator[T] extends Authenticator[T] with Hashing {
  abstract override def authenticate(user: String, password: String) = super.authenticate(user, hash(user, password))
}