package com.mle.auth

import com.mle.util.Util._


/**
 * @author Mle
 */
trait ConnectionProvider[T <: {def close()}] {
  /**
   *
   * @return a new connection object
   */
  def connection: T

  def withConnection[U](code: T => U): U = resource(connection)(code)
}
