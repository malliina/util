package com.mle.jdbc.tests

import com.mle.jdbc.auth.DefaultJdbcUserManager
import com.mle.auth.tests.UserManagementTests
import com.mle.jdbc.schema.UserMgmtSchema

/**
 *
 * @author mle
 */
class JdbcUserMgmtTests extends UserManagementTests {
  val manager = new DefaultJdbcUserManager(UserMgmtSchema(Tables.users, Tables.groups, Tables.usergroup))

  val authenticator = manager
}
