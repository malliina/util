package com.mle.jdbc.tests

import com.mle.jdbc.auth.DefaultJdbcUserManager
import com.mle.jdbc.DefaultSettings
import com.mle.auth.tests.UserManagementTests
import com.mle.jdbc.tests.TestSchema.UserMgmtTables

/**
 *
 * @author mle
 */
class JdbcUserMgmtTests extends UserManagementTests {
  val manager = new DefaultJdbcUserManager(UserMgmtTables)

  val authenticator = manager
}
