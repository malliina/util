package com.mle.auth.jdbc

import org.apache.tomcat.jdbc.pool.{DataSource, PoolProperties}
import com.mle.util.Log
import java.util.Properties
import collection.JavaConversions._

class MySQLConnectionProvider(url: String,
                              user: String,
                              password: Option[String],
                              keystoreSettings: Option[KeystoreSettings] = None)
  extends SQLConnectionProvider with Log {
  protected val p = new PoolProperties()
  p setUrl url
  p setDriverClassName "com.mysql.jdbc.Driver"
  p setUsername user
  password foreach (pass => p setPassword pass)
  keystoreSettings foreach (kss => p setDbProperties toSslProperties(kss))

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

  private def toSslProperties(keySettings: KeystoreSettings) = {
    lazy val sslProperties = new Properties
    sslProperties("useSSL") = "true"
    sslProperties("clientCertificateKeyStoreUrl") = keySettings.keystoreUrl.toString
    sslProperties("clientCertificateKeyStorePassword") = keySettings.keystorePass
    sslProperties("trustCertificateKeyStoreUrl") = keySettings.truststoreUrl.toString
    sslProperties("trustCertificateKeyStorePassword") = keySettings.truststorePass
    sslProperties
  }

  def connection = ds.getConnection
}
