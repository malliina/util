package com.malliina.auth.tests

import com.malliina.auth.ldap.{GroupDnInfo, LDAPConnectionProvider, LdapAttributes, DnInfo}
import com.malliina.security.ClientKeystoreSettings
import com.malliina.util.Util

/**
 *
 * @author mle
 */
object LdapTestSettings {
  val ldapProps = Util.props("conf/security/auth.test")
  val uri = ldapProps("ldap.uri")
  val adminUser = ldapProps("ldap.user")
  val adminPass = ldapProps("ldap.pass")
  val adminInfo = new DnInfo("cn", "dc=mle,dc=com")
  val peopleInfo = new DnInfo("uid", "ou=People,dc=mle,dc=com")
  val groupInfo = GroupDnInfo("cn", "ou=Groups,dc=mle,dc=com", LdapAttributes.memberuid)
  val hostInfo = GroupDnInfo("cn", "ou=Hosts,dc=mle,dc=com", LdapAttributes.member)
  // Fix if we ever use START_TLS with ldap:// uris
  val keySettings = if (uri startsWith "ldaps") Some(ClientKeystoreSettings) else None
  val connProvider = new LDAPConnectionProvider(
    uri,
    adminUser,
    Some(adminPass),
    adminInfo,
    keySettings = keySettings
  )
}
