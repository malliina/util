package com.mle.jdbc.schema

import java.sql.ResultSet

/**
 *
 * @author mle
 */
trait Mapping[T] extends Table {
  def mapper: ResultSet => T

  def select = db.query(allSql)(mapper)

  def selectWhere(col: String, value: Any) = {
    val whereClause = " where " + col + "=?"
    db.query(allSql + whereClause, value)(mapper)
  }
}
