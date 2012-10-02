package com.mle.auth.ldap


/**
 * @author Mle
 */
object TestLdapConnectionProvider extends LDAPConnectionProvider {
  val uri = "ldap://10.0.0.33:389"
  val userDnSuffix = ",ou=People,dc=mle,dc=com"
  val userDnPrefix = "uid="
}