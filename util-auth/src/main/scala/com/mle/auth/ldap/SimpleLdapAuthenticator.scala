package com.mle.auth.ldap

import javax.naming.directory.InitialDirContext
import java.util.Properties
import javax.naming.Context
import com.sun.jndi.ldap.LdapCtxFactory
import collection.JavaConversions._
import com.mle.util.Log
import com.mle.auth.Authenticator


/**
 * @author Mle
 */
class SimpleLdapAuthenticator(uri: String, userInfo: DnInfo, authMechanism: String = "simple")
  extends Authenticator[InitialDirContext] with Log {

  /**
   *
   * @param username
   * @param password
   * @return
   * @throws javax.naming.CommunicationException if the LDAP server cannot be reached
   * @throws javax.naming.InvalidNameException if some parameter is incorrect (invalid DN)
   * @throws javax.naming.AuthenticationException if the credentials are incorrect
   */
  override def authenticate(username: String, password: String) = {
    val props = new Properties()
    props(Context.SECURITY_PROTOCOL) = "ssl"
    props(Context.SECURITY_AUTHENTICATION) = authMechanism
    props(Context.INITIAL_CONTEXT_FACTORY) = classOf[LdapCtxFactory].getName
    props(Context.PROVIDER_URL) = uri
    props(Context.REFERRAL) = "ignore"
    props(Context.SECURITY_PRINCIPAL) = userInfo.toDN(username)
    props(Context.SECURITY_CREDENTIALS) = password
    val ret = new InitialDirContext(props)
    log debug "Connected to " + props(Context.PROVIDER_URL) + " with user DN: " + props(Context.SECURITY_PRINCIPAL)
    ret
  }
}