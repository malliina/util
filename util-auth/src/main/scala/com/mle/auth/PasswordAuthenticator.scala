package com.mle.auth

import crypto.Hashing

/**
 * This is bullshit and should be part of trait [[com.mle.auth.UserManager]]
 * @tparam T type of user
 * @author Mle
 */
trait PasswordAuthenticator[T] {
  def authenticate(credential: UserPassContainer): T = authenticate(credential.username, credential.password)

  def authenticate(user: String, password: String): T
}

trait AuthHashing[T] extends PasswordAuthenticator[T] with Hashing {
  abstract override def authenticate(user: String, password: String) = super.authenticate(user, hash(user, password))
}