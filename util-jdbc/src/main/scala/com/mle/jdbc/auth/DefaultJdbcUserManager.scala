package com.mle.jdbc.auth

import com.mle.auth.crypto.PasswordHashing
import com.mle.auth.ChangeLogging
import com.mle.jdbc.schema.UserMgmtSchema

/**
 *
 * @author mle
 */
class DefaultJdbcUserManager(schema: UserMgmtSchema)
  extends JDBCUserManager(schema)
  with ChangeLogging
  with PasswordHashing[String] {
}