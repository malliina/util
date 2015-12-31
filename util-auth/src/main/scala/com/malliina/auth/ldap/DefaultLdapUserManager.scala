package com.malliina.auth.ldap

import com.malliina.auth.ChangeLogging
import com.malliina.auth.crypto.PasswordHashing

/**
 * @author Mle
 */
class DefaultLdapUserManager(connectionProvider: LDAPConnectionProvider,
                             userInfo: DnInfo, groupInfo: GroupDnInfo)
  extends AbstractLdapUserManager(connectionProvider, userInfo, groupInfo)
  with ChangeLogging[String]
//  with PasswordHashing[String]