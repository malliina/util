package com.mle.auth.ldap

import com.mle.auth.ConnectionProvider
import javax.naming.directory.InitialDirContext
import javax.naming.Context
import com.sun.jndi.ldap.LdapCtxFactory
import java.util.Properties
import collection.JavaConversions._

/**
 * TODO: optional ssl
 *
 * @author Mle
 */
class LDAPConnectionProvider(uri: String,
                             user: String,
                             password: Option[String],
                             adminInfo: DnInfo,
                             authMechanism: String = "simple",
                             ssl: Boolean = true)
  extends ConnectionProvider[InitialDirContext] {
  private val sslSetting = if (ssl)
    Map(Context.SECURITY_PROTOCOL -> "ssl")
  else
    Map.empty[String, String]

  val noUserProperties = sslSetting ++ Map(
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
}
