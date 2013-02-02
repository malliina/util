package com.mle.ldap

import com.mle.util.Util
import com.mle.auth.ldap._
import com.mle.util.security.ClientKeystoreSettings
import com.mle.auth.ldap.DnInfo
import scala.Some
import com.mle.wicket.backend.{LdapUserProvider, WicketUserManager}

/**
 * Settings for wicket.
 *
 * @author mle
 */
object LdapSettings {
  val ldapProps = Util.props("conf/security/wicketauth.test")
  val uri = ldapProps("ldap.uri")
  val adminUser = ldapProps("ldap.user")
  val adminPass = ldapProps("ldap.pass")
  val adminInfo = new DnInfo("cn", "dc=mle,dc=com")
  val peopleInfo = new DnInfo("uid", "ou=People,dc=mle,dc=com")
  val groupInfo = GroupDnInfo("cn", "ou=Groups,dc=mle,dc=com", LdapAttributes.memberuid)
  val hostInfo = GroupDnInfo("cn", "ou=Hosts,dc=mle,dc=com", LdapAttributes.member)
  val maybeKeySettings = if (uri startsWith "ldaps") Some(ClientKeystoreSettings) else None
  val connProvider = new LDAPConnectionProvider(
    uri,
    adminUser,
    Some(adminPass),
    adminInfo,
    keySettings = maybeKeySettings
  )
  val hostManager = new LdapHostManager
  val userManager = new BasicUserManager
  val ldapUserManager = new LdapWicketUserManager

  class LdapWicketUserManager extends DefaultLdapUserManager(connProvider, peopleInfo, groupInfo)
  with WicketUserManager with LdapUserProvider {
    val hostManager = LdapSettings.hostManager
  }

  class BasicUserManager extends DefaultLdapUserManager(connProvider, peopleInfo, groupInfo)
  with WicketUserManager

  class LdapHostManager extends DefaultLdapUserManager(connProvider, peopleInfo, hostInfo)
  with LdapHostAdder
  with HostProvider
  with WicketUserManager

}
