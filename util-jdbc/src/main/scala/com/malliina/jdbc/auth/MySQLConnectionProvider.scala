package com.malliina.jdbc.auth

import java.util.Properties

import com.malliina.security.IKeystoreSettings
import com.malliina.util.Log
import org.apache.tomcat.jdbc.pool.{DataSource, PoolProperties}

import scala.collection.JavaConversions._

/**
 * Uses tomcat jdbc pools.
 *
 * @param url
 * @param user
 * @param password
 * @param keystoreSettings
 */
class MySQLConnectionProvider(url: String,
                              user: String,
                              password: Option[String],
                              keystoreSettings: Option[IKeystoreSettings] = None)
  extends SQLConnectionProvider with Log {
  protected val p = new PoolProperties()
  p setUrl url
  p setDriverClassName classOf[com.mysql.jdbc.Driver].getName
  p setUsername user
  password foreach (pass => p setPassword pass)
  keystoreSettings foreach (kss => p setDbProperties MySQLConnectionProvider.toSslProperties(kss))

  p setTestWhileIdle false
  p setTestOnBorrow true
  p setValidationQuery "SELECT 1"
  p setTestOnReturn false
  p setValidationInterval 30000
  p setTimeBetweenEvictionRunsMillis 30000
  p setMaxActive 100
  p setInitialSize 3
  p setMaxWait 10000
  p setRemoveAbandonedTimeout 60
  p setMinEvictableIdleTimeMillis 30000
  p setMinIdle 10
  p setLogAbandoned true
  p setRemoveAbandoned true
  //  p setJdbcInterceptors ("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
  //    "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer")
  val ds = new DataSource(p)

  def connection = ds.getConnection
}

object MySQLConnectionProvider {
  def toMap(keySettings: IKeystoreSettings) = Map(
    "useSSL" -> "true",
    "clientCertificateKeyStoreUrl" -> keySettings.keystoreUrl.toString,
    "clientCertificateKeyStorePassword" -> keySettings.keystorePass,
    "trustCertificateKeyStoreUrl" -> keySettings.truststoreUrl.toString,
    "trustCertificateKeyStorePassword" -> keySettings.truststorePass
  )

  def toSslProperties(keySettings: IKeystoreSettings) = {
    val sslProperties = new Properties()
    sslProperties putAll toMap(keySettings)
    sslProperties
  }
}
