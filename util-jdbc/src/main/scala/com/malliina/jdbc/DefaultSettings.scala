package com.malliina.jdbc

import com.malliina.jdbc.auth.MySQLConnectionProvider
import com.malliina.security.ClientKeystoreSettings
import com.malliina.util.Util

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
    keystoreSettings = Some(ClientKeystoreSettings)
  )
}
