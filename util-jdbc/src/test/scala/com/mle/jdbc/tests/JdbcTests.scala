package com.mle.jdbc.tests

import org.scalatest.FunSuite
import com.mle.util.Log
import com.mle.jdbc.auth.DefaultJdbcUserManager
import com.mle.jdbc.tests.TestSchema._
import com.mle.jdbc.schema.UserMgmtSchema

/**
 *
 * @author mle
 */
class JdbcTests extends FunSuite with Log {
  val userManager = new DefaultJdbcUserManager(UserMgmtSchema(Tables.users, Tables.groups, Tables.usergroup))
  val testUser = "jack"

  test("can perform simple query on mysql schema") {
    TestDb.query("select user,host from mysql.user")(rs => UserHost(rs getString 1, rs getString 2))
    //    log info "Got " + users.size + " users: " + users.mkString(", ")
  }
  test("can reflect schema from code") {
    assert(Test.tableName === "Test")
    assert(Test.myCol.name === "myCol")
  }

  case class UserHost(user: String, host: String)

}
