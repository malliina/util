package com.mle.jdbc.auth

import com.mle.util.security.IKeystoreSettings
import com.jolbox.bonecp.BoneCPDataSource
import MySQLConnectionProvider._

/**
 * Got some strange lock problems. Preferring tomcat pooling for now.
 */
class BoneCpConnectionProvider(url: String,
                               user: String,
                               password: Option[String],
                               keystoreSettings: Option[IKeystoreSettings] = None)
  extends SQLConnectionProvider {
  Class forName classOf[com.mysql.jdbc.Driver].getName
  val ds = new BoneCPDataSource()
  ds setJdbcUrl url
  ds setUsername user
  password foreach ds.setPassword
  keystoreSettings foreach (kss => ds setDriverProperties toSslProperties(kss))

  def connection = ds.getConnection

  def toDriverProperties(keySettings: IKeystoreSettings) = toMap(keySettings).mkString(";")
}
