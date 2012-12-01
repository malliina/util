package com.mle.auth.ldap

/**
 *
 * @author mle
 */
object LdapAttributes extends Enumeration {
  type LdapAttribute = Value
  // posixAccount, mandatory
  val cn, uid, uidNumber, gidNumber, homeDirectory = Value
  // posixAccount, optional
  val userPassword, loginShell = Value
  // posixGroup, member DNs
  val memberuid = Value
  // misc
  val objectClass = Value
  // objectClasses
  val posixAccount, shadowAccount, posixGroup, inetOrgPerson, groupOfNames, ipHost = Value
  // inetOrgPerson
  val sn = Value
  // PAM groupdn
  val member, ipHostNumber = Value
}
