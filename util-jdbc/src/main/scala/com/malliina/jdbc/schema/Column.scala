package com.malliina.jdbc.schema

/**
 *
 * @author mle
 */
trait Column {
  def schema: Schema

  def table: Table

  def name: String

  def size: Option[Int] = None

  def defaultValue: Option[String] = None

  override def toString = table + "." + name
}
