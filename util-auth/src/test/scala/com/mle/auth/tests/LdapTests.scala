package com.mle.auth.tests

import com.mle.util.{Log, Util}
import com.mle.util.security.ClientKeystoreSettings
import com.mle.auth.ldap.{DefaultLdapUserManager, LDAPConnectionProvider, DnInfo, LdapDirInfo}

/**
 * set fork := True when running from sbt
 *
 * @author mle
 */
class LdapTests extends UserManagementTests with Log {
  val ldapProps = Util.props("conf/security/auth.test")
  val uri = ldapProps("ldap.uri")
  log info "Testing LDAP at URI: " + uri
  val adminUser = ldapProps("ldap.user")
  val adminPass = ldapProps("ldap.pass")
  val adminInfo = DnInfo("cn", "dc=mle,dc=com")
  val peopleInfo = DnInfo("uid", "ou=People,dc=mle,dc=com")
  val groupInfo = DnInfo("cn", "ou=Groups,dc=mle,dc=com")
  val schema = LdapDirInfo(uri, adminInfo, peopleInfo, groupInfo)
  // Fix if we use START_TLS with ldap:// uris
  val keySettings = if (uri startsWith "ldaps") Some(ClientKeystoreSettings) else None
  val connProvider = new LDAPConnectionProvider(
    schema.uri,
    adminUser,
    Some(adminPass),
    schema.adminInfo,
    //    keySettings = Some(ClientKeystoreSettings)
    keySettings = keySettings
  )
  val manager = new DefaultLdapUserManager(connProvider, schema.usersInfo, schema.groupsInfo)
  val authenticator = manager
}