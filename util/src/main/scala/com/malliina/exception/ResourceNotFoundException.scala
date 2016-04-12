package com.malliina.exception

/**
 * @author Mle
 */
class ResourceNotFoundException(msg: String, t: Throwable) extends GenericException(msg, t) {
  def this(msg: String) = this(msg, null)
}