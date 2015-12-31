package com.malliina.jdbc

import java.sql.PreparedStatement

/**
 *
 * @author mle
 */
trait StatementLogging extends Database {
  override def statement[T](sql: String, params: Any*)(code: PreparedStatement => T) = {
    val paramDescription = if (params.isEmpty) "" else params.mkString(" with parameters: ", ", ", "")
    log info "SQL: '" + sql + "'" + paramDescription
    super.statement(sql, params: _*)(code)
  }
}
