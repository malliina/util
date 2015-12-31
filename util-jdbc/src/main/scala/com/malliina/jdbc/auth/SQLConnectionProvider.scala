package com.malliina.jdbc.auth

import com.malliina.auth.ConnectionProvider
import java.sql.{PreparedStatement, Connection}
import com.malliina.util.Util._
import com.malliina.util.Log

/**
 *
 * @author mle
 */
trait SQLConnectionProvider extends ConnectionProvider[Connection] with Log {
  override def withConnection[T](code: Connection => T) = using(connection)(code)

  def withStmt[T](sql: String)(code: PreparedStatement => T) = withConnection(conn => {
    using(conn.prepareStatement(sql))(code)
  })
}
