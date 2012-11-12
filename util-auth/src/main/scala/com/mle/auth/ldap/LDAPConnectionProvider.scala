package com.mle.auth.ldap

import com.mle.auth.ConnectionProvider
import javax.naming.directory.InitialDirContext
import javax.naming.Context
import com.sun.jndi.ldap.LdapCtxFactory
import java.util.Properties
import collection.JavaConversions._
import com.mle.util.Log
import com.mle.util.security.IKeystoreSettings
import javax.net.SocketFactory

/**
 *
 * @param uri
 * @param user
 * @param password
 * @param adminInfo
 * @param authMechanism
 */
class LDAPConnectionProvider(uri: String,
                             user: String,
                             password: Option[String],
                             adminInfo: DnInfo,
                             keySettings: Option[IKeystoreSettings] = None,
                             authMechanism: String = "simple")
  extends ConnectionProvider[InitialDirContext] with Log {
  // Context.SECURITY_PROTOCOL is redundant if using ldaps uris
  private val sslProperties = keySettings.map(_ =>
    Map(Context.SECURITY_PROTOCOL -> "ssl", "java.naming.ldap.factory.socket" -> classOf[LdapSocketFactory].getName)
  ).getOrElse(Map.empty[String, String])

  val noUserProperties = sslProperties ++ Map(
    Context.SECURITY_AUTHENTICATION -> authMechanism,
    Context.INITIAL_CONTEXT_FACTORY -> classOf[LdapCtxFactory].getName,
    Context.PROVIDER_URL -> uri,
    Context.REFERRAL -> "ignore"
  )

  private[this] val connectionProperties: Map[String, String] = noUserProperties ++ Map(
    Context.SECURITY_PRINCIPAL -> adminInfo.toDN(user),
    Context.SECURITY_CREDENTIALS -> password.getOrElse(""),
    "com.sun.jndi.ldap.connect.timeout" -> "3000", // in ms
    "com.sun.jndi.ldap.connect.pool" -> "true", // enable connection pooling
    "com.sun.jndi.ldap.connect.pool.timeout" -> "86400000", // idle connection timeout, in ms (24h)
    "com.sun.jndi.ldap.connect.pool.protocol" -> "plain ssl" // allow ssl connections to be pooled
  )
  private[this] val props = new Properties()
  props putAll connectionProperties

  /**
   *
   * @return a new connection object
   * @throws NamingSecurityException
   */
  def connection = new InitialDirContext(props)

  def authMechanisms(uri: String) = {
    withConnection(_.getAttributes(uri, Array("supportedSASLMechanisms")))
  }

  // if SSL is used, uses this custom SSL socket factory derived from the supplied keystore settings
  class LdapSocketFactory(keySettings: IKeystoreSettings) extends MySocketFactory(keySettings)

  object LdapSocketFactory {
    val socketFactory = keySettings.map(ks => new MySocketFactory(ks)).getOrElse(SocketFactory.getDefault)

    val getDefault: SocketFactory = socketFactory
  }

}
