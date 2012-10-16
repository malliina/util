package com.mle.jdbc.tests

import com.mle.jdbc.auth.JDBCUserManager
import com.mle.jdbc.DefaultSettings

/**
 *
 * @author mle
 */
class JdbcUserMgmtTests extends UserManagementTests {
  val manager = new JDBCUserManager(DefaultSettings.connProvider)
}
