package com.malliina.jdbc.auth

import com.malliina.auth.crypto.PasswordHashing
import com.malliina.auth.ChangeLogging
import com.malliina.jdbc.schema.UserMgmtSchema

/**
 *
 * @author mle
 */
class DefaultJdbcUserManager(schema: UserMgmtSchema)
  extends JDBCUserManager(schema)
  with ChangeLogging[String]
  with PasswordHashing[String] {
}