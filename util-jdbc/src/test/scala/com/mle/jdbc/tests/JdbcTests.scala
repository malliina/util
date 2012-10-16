package com.mle.jdbc.tests

import org.scalatest.FunSuite
import com.mle.util.Log
import com.mle.jdbc.{DefaultSettings, DB}
import com.mle.jdbc.auth.JDBCUserManager

/**
 *
 * @author mle
 */
class JdbcTests extends FunSuite with Log {
  val userManager = new JDBCUserManager(DefaultSettings.connProvider)
  val testUser = "jack"

  test("can perform simple query on mysql schema") {
    DB.query("select user,host from mysql.user")(rs => UserHost(rs getString 1, rs getString 2))
    //    log info "Got " + users.size + " users: " + users.mkString(", ")
  }
  test("can add user, read users and remove user") {
    userManager.addUser(testUser, "jack")
    assert(userManager.users contains testUser)
    userManager removeUser testUser
    assert(!(userManager.users contains testUser))
  }

  case class UserHost(user: String, host: String)

}
