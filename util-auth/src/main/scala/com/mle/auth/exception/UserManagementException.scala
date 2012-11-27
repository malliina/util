package com.mle.auth.exception

/**
 *
 * @author mle
 */
class UserManagementException(msg: String, t: Throwable) extends Exception(msg, t) {
  def this(msg: String) = this(msg, null)
}
