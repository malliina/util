package com.mle.auth.jdbc

import com.mle.util.{Util, Log}

/**
 *
 * @author mle
 */
object JDBCTest extends Log {
  def main(args: Array[String]) {
    val dbInfo = Util.props("conf/security/auth.test")
    val connProvider = new MySQLConnectionProvider(
      dbInfo("db.uri"),
      dbInfo("db.user"),
      Some(dbInfo("db.pass")),
      keystoreSettings = Some(DefaultKeystoreSettings)
    )
    val db = new Database(connProvider)
    val users = db.query("select user,host from mysql.user")(rs => UserHost(rs getString 1, rs getString 2))
    log info "Got " + users.size + " users: " + users.mkString(", ")
  }

  case class UserHost(user: String, host: String)

}
