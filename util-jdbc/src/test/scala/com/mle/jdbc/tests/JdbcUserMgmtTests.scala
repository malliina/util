package com.mle.jdbc.tests

import com.mle.jdbc.auth.JDBCUserManager
import com.mle.jdbc.DefaultSettings
import com.mle.auth.tests.UserManagementTests

/**
 *
 * @author mle
 */
class JdbcUserMgmtTests extends UserManagementTests {
  val manager = new JDBCUserManager(DefaultSettings.connProvider)

  val authenticator = manager
}
