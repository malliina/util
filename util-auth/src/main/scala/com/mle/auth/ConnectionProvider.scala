package com.mle.auth

import com.mle.util.Utils


/**
 * @author Mle
 */
trait ConnectionProvider[T <: {def close()}] {
  /**
   *
   * @return a new connection object
   */
  def connection: T

  def withConnection[U](code: T => U): U = Utils.resource(connection)(code)
}
