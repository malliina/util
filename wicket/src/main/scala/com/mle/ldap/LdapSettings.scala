package com.mle.ldap

import com.mle.util.security.ClientKeystoreSettings
import com.mle.util.Util
import com.mle.auth.ldap.{DefaultLdapUserManager, LDAPConnectionProvider, LdapDirInfo, DnInfo}

/**
 *
 * @author mle
 */
object LdapSettings {
  val ldapProps = Util.props("conf/security/wicketauth.test")
  val uri = ldapProps("ldap.uri")
  val adminUser = ldapProps("ldap.user")
  val adminPass = ldapProps("ldap.pass")
  val adminInfo = DnInfo("cn", "dc=mle,dc=com")
  val peopleInfo = DnInfo("uid", "ou=People,dc=mle,dc=com")
  val groupInfo = DnInfo("cn", "ou=Groups,dc=mle,dc=com")
  val schema = LdapDirInfo(uri, adminInfo, peopleInfo, groupInfo)
  val connProvider = new LDAPConnectionProvider(schema.uri, adminUser, Some(adminPass), schema.adminInfo, keySettings = Some(ClientKeystoreSettings))
  val manager = new DefaultLdapUserManager(connProvider, schema.usersInfo, schema.groupsInfo)
}
