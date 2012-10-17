package com.mle.auth.tests

import com.mle.util.Util
import com.mle.auth.ldap.{SimpleLdapAuthenticator, LDAPUserManager, DnInfo, LdapDirInfo}
import com.mle.util.security.DefaultKeystoreSettings

/**
 * set fork := True when running from sbt
 *
 * @author mle
 */
class LdapTests extends UserManagementTests {
  DefaultKeystoreSettings.setSystemProperties()
  val ldapProps = Util.props("conf/security/auth.test")
  val uri = ldapProps("ldap.uri")
  val peopleInfo = DnInfo("uid", "ou=People,dc=mle,dc=com")
  val groupInfo = DnInfo("cn", "ou=Groups,dc=mle,dc=com")
  val authenticator = new SimpleLdapAuthenticator(uri, peopleInfo)
  val schema = LdapDirInfo(uri,
    DnInfo("cn", "dc=mle,dc=com"),
    peopleInfo,
    groupInfo
  )
  val manager = LDAPUserManager(schema, ldapProps("ldap.user"), ldapProps("ldap.pass"), logging = false)
}