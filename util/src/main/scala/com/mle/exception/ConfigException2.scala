package com.mle.exception

/**
 * @author Mle
 */
class ConfigException2(msg: String, t: Throwable) extends GenericException(msg, t) {
  def this(msg: String) = this(msg, null)
}
