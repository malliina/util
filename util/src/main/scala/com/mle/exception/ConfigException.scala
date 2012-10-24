package com.mle.exception

/**
 * @author Mle
 */
class ConfigException(msg: String, t: Throwable) extends Exception(msg, t) {
  def this(msg: String) = this(msg, null)
}
