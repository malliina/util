package com.malliina.auth

import com.malliina.util.Utils


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
