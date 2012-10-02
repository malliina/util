package com.mle.auth.ldap

import javax.naming.directory.{SearchControls, InitialDirContext}
import java.util.Properties
import javax.naming.Context
import com.sun.jndi.ldap.LdapCtxFactory
import collection.JavaConversions._
import com.mle.util.Log


/**
 * @author Mle
 */
trait LDAPConnectionProvider extends Log {
  def uri: String

  def userDnSuffix: String

  def userDnPrefix: String

  def toDN(username: String) = userDnPrefix + username + userDnSuffix

  /**
   *
   * @param username
   * @param password
   * @return
   * @throws javax.naming.CommunicationException if the LDAP server cannot be reached
   * @throws javax.naming.InvalidNameException if some parameter is incorrect (invalid DN)
   * @throws javax.naming.AuthenticationException if the credentials are incorrect
   */
  def login(username: String, password: String) = {
    val props = new Properties()
    props(Context.SECURITY_AUTHENTICATION) = "simple"
    props(Context.INITIAL_CONTEXT_FACTORY) = classOf[LdapCtxFactory].getName
    props(Context.PROVIDER_URL) = uri
    props(Context.REFERRAL) = "ignore"
    props(Context.SECURITY_PRINCIPAL) = toDN(username)
    props(Context.SECURITY_CREDENTIALS) = password
    val ret = new InitialDirContext(props)
    log info "Connected to " + props(Context.PROVIDER_URL) + " with user DN: " + props(Context.SECURITY_PRINCIPAL)
    ret
  }
  def attribute(name:String,ctx:InitialDirContext)={
    toDN(name)
  }
  def attributez(attributeName: String, ctx: InitialDirContext) = {
    val searchControls = new SearchControls()
    searchControls setSearchScope SearchControls.SUBTREE_SCOPE
    val filter = "(&(objectClass=inetOrgPerson)(uid=john))"
    val returnedAttrs = Array("displayName")
    searchControls setReturningAttributes returnedAttrs
    val answer = ctx.search("dc=mle,dc=com", filter, searchControls)
    if (!answer.hasMore)
      log info "No results"
    val attrs = answer.flatMap(_.getAttributes.getAll.map(attr => String.valueOf(attr.get())))
    log info "" + attrs.mkString(", ")
  }
}