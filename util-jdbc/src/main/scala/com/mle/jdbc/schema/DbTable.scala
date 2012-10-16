package com.mle.jdbc.schema

import com.mle.jdbc.DB

/**
 *
 * @author mle
 */
abstract class DbTable(name: String) extends Table(name) {
  def db = DB

  def schema = DB.schema

  override def toString = schema + "." + name
}
