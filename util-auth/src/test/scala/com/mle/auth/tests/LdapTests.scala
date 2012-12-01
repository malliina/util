package com.mle.auth.tests

import com.mle.util.Log
import com.mle.auth.ldap._
import LdapTestSettings._

/**
 * set fork := True when running from sbt
 *
 * @author mle
 */
class LdapTests extends UserManagementTests with Log {
  val manager = new DefaultLdapUserManager(connProvider, peopleInfo, groupInfo)
  val authenticator = manager
}