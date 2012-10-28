package com.mle.jdbc.auth

import com.mle.util.security.IKeystoreSettings
import com.jolbox.bonecp.BoneCPDataSource
import MySQLConnectionProvider._

class BoneCpConnectionProvider(url: String,
                               user: String,
                               password: Option[String],
                               keystoreSettings: Option[IKeystoreSettings] = None) extends SQLConnectionProvider {
  Class forName classOf[com.mysql.jdbc.Driver].getName
  val ds = new BoneCPDataSource()
  ds setJdbcUrl url
  ds setUser user
  password foreach ds.setPassword
  keystoreSettings foreach (kss => ds setDriverProperties toSslProperties(kss))

  def connection = ds.getConnection

  def toDriverProperties(keySettings: IKeystoreSettings) = toMap(keySettings).mkString(";")
}
