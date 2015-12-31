package com.malliina.jdbc

import auth.SQLConnectionProvider
import java.sql.{PreparedStatement, ResultSet}
import com.malliina.util.Util._
import com.malliina.util.Log


/**
 * TODO: Explore Slick when 2.10 is out
 *
 * @author mle
 */
class Database(connectionProvider: SQLConnectionProvider) extends Log {
  implicit def rs2iter(rs: ResultSet): Iterator[ResultSet] = Iterator.continually(if (!rs.isClosed && rs.next()) rs else null).takeWhile(_ != null)

  def query[T](sql: String, params: Any*)(rsCode: ResultSet => T): Seq[T] =
    statement(sql, params: _*)(stmt =>
      using(stmt.executeQuery())(rs => rs.map(rsCode).toList) // FYI, toSeq doesn't work here
    )

  def find[T](sql: String, params: Any*)(rsMapping: ResultSet => T): Option[T] =
    query(sql, params: _*)(rsMapping).headOption

  def head[T](sql: String, params: Any*)(code: ResultSet => T) = find(sql, params: _*)(code)
    .getOrElse(throw new NoSuchElementException("No results for '" + sql + "' with params: " + params.mkString(", ")))

  def execute(sql: String, params: Any*) {
    statement(sql, params: _*)(_.execute())
  }

  def statement[T](sql: String, params: Any*)(code: PreparedStatement => T) = {
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
