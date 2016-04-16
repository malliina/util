package com.malliina.exception

class SecurityException(msg: String, t: Throwable) extends GenericException(msg, t) {
  def this(msg: String) = this(msg, null)
}
