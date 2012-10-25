package com.mle.auth.ldap

import com.mle.auth.{HashingAuthenticator, ChangeLogging}
import com.mle.auth.crypto.PasswordHashing
import javax.naming.directory.InitialDirContext

/**
 * @author Mle
 */
class DefaultLdapUserManager(connectionProvider: LDAPConnectionProvider, userInfo: DnInfo, groupInfo: DnInfo)
  extends AbstractLdapUserManager(connectionProvider, userInfo, groupInfo)
  with ChangeLogging
  with PasswordHashing[InitialDirContext]