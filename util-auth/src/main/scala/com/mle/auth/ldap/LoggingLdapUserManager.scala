package com.mle.auth.ldap

import com.mle.auth.ChangeLogging

/**
 * @author Mle
 */
class LoggingLdapUserManager(connectionProvider: LDAPConnectionProvider, userInfo: DnInfo, groupInfo: DnInfo)
  extends LDAPUserManager(connectionProvider, userInfo, groupInfo) with ChangeLogging