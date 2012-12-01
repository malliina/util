package com.mle.auth.ldap

import com.mle.auth.MembershipManagerImpl
import com.mle.auth.ldap.UserHostAuthorizer.Host
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