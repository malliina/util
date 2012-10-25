package com.mle.auth.tests

import org.scalatest.{BeforeAndAfter, FunSuite}
import com.mle.auth.{Authenticator, UserManager}
import com.mle.util.Util

/**
 * Use dependsOn(utilAuthModule % "test->test") to include this in other modules' test classpaths.
 *
 * @author mle
 */
abstract class UserManagementTests extends FunSuite with BeforeAndAfter {
  val testUser = "testUser"
  val testGroup = "testGroup"
  val testPassword = "TOP_SECRET_CLASSIFIED_X-GRADE"

  def manager: UserManager

  def authenticator: Authenticator[_]

  after {
    Util optionally (manager removeUser testUser)
    Util optionally (manager removeGroup testGroup)
  }

  test("server is reachable") {
    manager.users
  }
  test("user add/remove") {
    val usersBefore = manager.users
    manager addUser(testUser, testPassword)
    val e = intercept[Exception] {
      // NameAlreadyBoundException for LDAP
      // MySQLIntegrityConstraintViolationException (SQLException) for MySQL
      manager addUser(testUser, testPassword)
    }
    //    println(e.getClass.getSimpleName + ": " + e.getMessage)
    assert(manager.users contains testUser)
    manager removeUser testUser
    assert(usersBefore === manager.users)
  }
  test("group add/remove") {
    val groupsBefore = manager.groups
    manager addGroup testGroup
    intercept[Exception] {
      // NameAlreadyBoundException for LDAP
      // SQLException for MySQL
      manager addGroup testGroup
    }
    assert(manager.groups contains testGroup)
    manager removeGroup testGroup
    assert(groupsBefore === manager.groups)
  }

  test("group membership") {
    val initialUsers = manager.users
    val initialGroups = manager.groups
    manager addGroup testGroup
    manager addUser(testUser, testPassword)
    manager assign(testUser, testGroup)
    assert(manager.groups contains testGroup)
    assert(manager.users(testGroup) === Seq(testUser))
    manager revoke(testUser, testGroup)
    assert(manager.users(testGroup) === Seq.empty)
    manager assign(testUser, testGroup)
    manager removeUser testUser
    assert(manager.users(testGroup) === Seq.empty)
    manager removeGroup testGroup
    assert(manager.users === initialUsers)
    assert(manager.groups === initialGroups)
  }
  test("passwd changes") {
    manager addUser(testUser, testPassword)
    authenticator authenticate(testUser, testPassword)
    manager setPassword(testUser, "temp")
    intercept[Exception] {
      // javax.naming.AuthenticationException for JNDI (LDAP)
      // AuthException for jdbc
      authenticator authenticate(testUser, testPassword)
    }
    manager setPassword(testUser, testPassword)
    authenticator authenticate(testUser, testPassword)
  }
}
