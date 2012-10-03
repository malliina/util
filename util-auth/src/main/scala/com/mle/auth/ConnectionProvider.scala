package com.mle.auth

/**
 * @author Mle
 */
trait ConnectionProvider[T] {
  /**
   *
   * @return a new connection object
   */
  def connection: T
}
