package com.mle.db

import com.mle.util.Util
import com.mle.jdbc.auth.{DefaultJdbcUserManager, MySQLConnectionProvider}
import com.mle.util.security.ClientKeystoreSettings
import com.mle.jdbc.{StatementLogging, Database}
import com.mle.jdbc.schema.{UserMgmtSchema, Schema}

/**
 *
 * @author mle
 */
object DatabaseSettings {
  val dbInfo = Util.props("conf/security/wicketauth.test")
  val uri = dbInfo("db.uri")
  val user = dbInfo("db.user")
  val pass = dbInfo("db.pass")

  object ConnectionPool extends MySQLConnectionProvider(uri, user, Some(pass), Some(ClientKeystoreSettings))

  object Db extends Database(ConnectionPool) with StatementLogging

  object MySchema extends Schema {
    val db = Db
    val name = "testdb"
  }

  val userMgmtSchema = UserMgmtSchema(Tables.users, Tables.groups, Tables.usergroup)
  val userManager = new DefaultJdbcUserManager(userMgmtSchema)
}