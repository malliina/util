package com.mle.jdbc.schema

/**
 *
 * @author mle
 */
trait Column {
  def schema: String

  def table: String

  def name: String

  def size: Option[Int] = None

  def defaultValue: Option[String] = None
}
