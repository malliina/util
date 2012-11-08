package com.mle.auth.crypto

import com.mle.auth.{AuthHashing, UserManager}

/**
 * TODO: Self-type?
 */
trait PasswordHashing[U] extends UserManager[String] with AuthHashing[U] {
  abstract override def setPassword(user: String, newPassword: String) {
    super.setPassword(user, hash(user, newPassword))
  }

  abstract override def addUser(user: String, password: String) {
    super.addUser(user, hash(user, password))
  }
}