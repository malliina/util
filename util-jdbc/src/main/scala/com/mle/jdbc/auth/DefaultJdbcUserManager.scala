package com.mle.jdbc.auth

import com.mle.auth.crypto.PasswordHashing
import com.mle.auth.{ChangeLogging, HashingAuthenticator}
import com.mle.jdbc.schema.UserMgmtSchema

/**
 *
 * @author mle
 */
class DefaultJdbcUserManager(connProvider: SQLConnectionProvider, schema: UserMgmtSchema)
  extends JDBCUserManager(connProvider, schema)
  with ChangeLogging
  with PasswordHashing
  with HashingAuthenticator[String] {
}