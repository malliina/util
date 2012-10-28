package com.mle.jdbc

import com.mle.util.security.ClientKeystoreSettings
import auth.BoneCpConnectionProvider
import com.mle.util.Util

/**
 *
 * @author mle
 */
object DefaultSettings {
  val dbInfo = Util.props("conf/security/auth.test")
  val connProvider = new BoneCpConnectionProvider(// MySQLConnectionProvider
    dbInfo("db.uri"),
    dbInfo("db.user"),
    Some(dbInfo("db.pass")),
    keystoreSettings = Some(ClientKeystoreSettings)
  )
}