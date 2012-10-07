package com.mle.auth.ldap

/**
 * @author Mle
 */
class LoggingLdapUserManager(connectionProvider: LDAPConnectionProvider, userInfo: DnInfo, groupInfo: DnInfo)
  extends LDAPUserManager(connectionProvider, userInfo, groupInfo) with ChangeLogging