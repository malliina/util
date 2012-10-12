package com.mle.auth.jdbc

import java.sql.{PreparedStatement, ResultSet}
import com.mle.util.Util._
import com.mle.util.Log


/**
 * TODO: Explore Slick when 2.10 is out
 * @author mle
 */
class Database(connectionProvider: SQLConnectionProvider) extends Log {
  implicit def rs2iter(rs: ResultSet) = Iterator.continually(if (!rs.isClosed && rs.next()) rs else null).takeWhile(_ != null)

  def query[T](sql: String, params: Any*)(rsCode: ResultSet => T): Seq[T] =
    statement(sql, params: _*)(stmt =>
      using(stmt.executeQuery())(rs => rs.map(rsCode).toList) // FYI, toSeq doesn't work here
    )

  def execute(sql: String, params: Any*) {
    statement(sql, params: _*)(_.execute())
  }

  def statement[T](sql: String, params: Any*)(code: PreparedStatement => T) = {
    val paramDescription = if (params.isEmpty) "" else params.mkString(" with parameters: ", ", ", "")
    log info "SQL: '" + sql + "'" + paramDescription
    val values = params.zipWithIndex
    connectionProvider.withStmt(sql)(stmt => {
      values foreach (pair => {
        val (value, position) = pair
        stmt setObject(position + 1, value)
      })
      code(stmt)
    })
  }
}
