package com.mle.auth.tests

import com.mle.util.Util
import com.mle.auth.ldap.{SimpleLdapAuthenticator, AbstractLdapUserManager, DnInfo, LdapDirInfo}
import com.mle.util.security.ClientKeystoreSettings
import com.mle.auth.HashingAuthenticator
import javax.naming.directory.InitialDirContext

/**
 * set fork := True when running from sbt
 *
 * @author mle
 */
class LdapTests extends UserManagementTests {
  ClientKeystoreSettings.setSystemProperties()
  val ldapProps = Util.props("conf/security/auth.test")
  val uri = ldapProps("ldap.uri")
  val adminInfo = DnInfo("cn", "dc=mle,dc=com")
  val peopleInfo = DnInfo("uid", "ou=People,dc=mle,dc=com")
  val groupInfo = DnInfo("cn", "ou=Groups,dc=mle,dc=com")
  val authenticator = new SimpleLdapAuthenticator(uri, peopleInfo) with HashingAuthenticator[InitialDirContext]
  val schema = LdapDirInfo(uri, adminInfo, peopleInfo, groupInfo)
  val manager = AbstractLdapUserManager(schema, ldapProps("ldap.user"), ldapProps("ldap.pass"), logging = true)
}