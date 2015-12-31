package com.malliina.auth.ldap

import com.malliina.auth.MembershipManagerImpl
import com.malliina.auth.ldap.UserHostAuthorizer.Host
import LdapHelper._

/**
 *
 * @author mle
 */
class UserHostAuthorizer(val connectionProvider: LDAPConnectionProvider,
                         val userInfo: DnInfo,
                         val groupInfo: GroupDnInfo)
  extends MembershipManagerImpl
  with LdapManager
  with LdapHostAdder {
//  def hosts: Seq[Host]=
}

object UserHostAuthorizer {

  case class Host(hostname: String, ip: String)

}