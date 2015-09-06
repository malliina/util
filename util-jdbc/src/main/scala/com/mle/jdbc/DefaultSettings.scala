package com.mle.jdbc

import com.mle.jdbc.auth.MySQLConnectionProvider
import com.mle.security.ClientKeystoreSettings
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
    keystoreSettings = Some(ClientKeystoreSettings)
  )
}
