package com.mle.jdbc

/**
 *
 * @author mle
 */
object DB extends Database(DefaultSettings.connProvider) {
  val schema = "testdb"
}