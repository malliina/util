package com.mle.jdbc

import auth.{DefaultKeystoreSettings, MySQLConnectionProvider}
import com.mle.util.Util

/**
 *
 * @author mle
 */
object DefaultSettings {
  val dbInfo = Util.props("conf/security/auth.test")
  val connProvider = new MySQLConnectionProvider(
    dbInfo("db.uri"),
    dbInfo("db.user"),
    Some(dbInfo("db.pass")),
    keystoreSettings = Some(DefaultKeystoreSettings)
  )
//  val db = new Database(connProvider)
}