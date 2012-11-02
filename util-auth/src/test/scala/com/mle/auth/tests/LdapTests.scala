package com.mle.auth.tests

import com.mle.util.Util
import com.mle.auth.ldap._
import com.mle.util.security.ClientKeystoreSettings
import com.mle.auth.ldap.DnInfo
import scala.Some
import com.mle.auth.ldap.LdapDirInfo

/**
 * set fork := True when running from sbt
 *
 * @author mle
 */
class LdapTests extends UserManagementTests {
  ClientKeystoreSettings.prepareSystemProperties()
  val ldapProps = Util.props("conf/security/auth.test")
  val uri = ldapProps("ldap.uri")
  val adminUser = ldapProps("ldap.user")
  val adminPass = ldapProps("ldap.pass")
  val adminInfo = DnInfo("cn", "dc=mle,dc=com")
  val peopleInfo = DnInfo("uid", "ou=People,dc=mle,dc=com")
  val groupInfo = DnInfo("cn", "ou=Groups,dc=mle,dc=com")
  val schema = LdapDirInfo(uri, adminInfo, peopleInfo, groupInfo)
  val connProvider = new LDAPConnectionProvider(schema.uri, adminUser, Some(adminPass), schema.adminInfo, ssl = true)
  val manager = new DefaultLdapUserManager(connProvider, schema.usersInfo, schema.groupsInfo)
  val authenticator = manager
}