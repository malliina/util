package com.mle.auth.crypto

import com.mle.auth.{HashingAuthenticator, UserManager}

/**
 *
 * @tparam T type of connection ([[javax.naming.directory.InitialDirContext]], [[java.sql.Connection]], ...)
 */
trait PasswordHashing[T] extends UserManager with HashingAuthenticator[T] {

  abstract override def setPassword(user: String, newPassword: String) {
    super.setPassword(user, hash(user, newPassword))
  }

  abstract override def addUser(user: String, password: String) {
    super.addUser(user, hash(user, password))
  }
}