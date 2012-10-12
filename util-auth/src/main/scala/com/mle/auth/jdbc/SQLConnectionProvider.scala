package com.mle.auth.jdbc

import com.mle.auth.ConnectionProvider
import java.sql.{PreparedStatement, Connection}
import com.mle.util.Util._

/**
 *
 * @author mle
 */
trait SQLConnectionProvider extends ConnectionProvider[Connection] {
  def withConnection[T](code: Connection => T) = using(connection)(code)

  def withStmt[T](sql: String)(code: PreparedStatement => T) = withConnection(conn => {
    using(conn.prepareStatement(sql))(code)
  })
}
