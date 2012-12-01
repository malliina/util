package com.mle.auth.ldap

import com.mle.auth.ChangeLogging
import com.mle.auth.crypto.PasswordHashing

/**
 * @author Mle
 */
class DefaultLdapUserManager(connectionProvider: LDAPConnectionProvider,
                             userInfo: DnInfo, groupInfo: GroupDnInfo)
  extends AbstractLdapUserManager(connectionProvider, userInfo, groupInfo)
  with ChangeLogging[String]
//  with PasswordHashing[String]