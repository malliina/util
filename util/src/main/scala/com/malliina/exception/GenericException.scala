package com.malliina.exception

/**
 * @author Mle
 */
abstract class GenericException(msg: String, t: Throwable) extends Exception(msg, t) {
  def this(msg: String) = this(msg, null)
}
