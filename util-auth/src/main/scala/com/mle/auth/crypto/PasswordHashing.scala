package com.mle.auth.crypto

import com.mle.auth.UserManager

/**
 *
 * @author mle
 */
trait PasswordHashing extends UserManager with Hashing {

  abstract override def setPassword(user: String, newPassword: String) {
    super.setPassword(user, hash(user, newPassword))
  }

  abstract override def addUser(user: String, password: String) {
    super.addUser(user, hash(user, password))
  }
}