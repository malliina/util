package com.mle.auth.tests

import org.scalatest.{BeforeAndAfter, FunSuite}
import com.mle.auth.{PasswordAuthenticator, UserManager}
import com.mle.util.{Utils, Util}

/**
 * Use dependsOn(utilAuthModule % "test->test") to include this in other modules' test classpaths.
 *
 * @author mle
 */
abstract class UserManagementTests extends FunSuite with BeforeAndAfter {
  val testUser = "testUser"
  val testGroup = "testGroup"
  val testGroup2 = "testGroup2"
  val testGroup3 = "testGroup3"
  val testPassword = "TOP_SECRET_CLASSIFIED_X-GRADE"

  def manager: UserManager[String]

  def authenticator: PasswordAuthenticator[_]

  after {
    cleanup()
  }

  def cleanup() {
    Utils optionally (manager removeUser testUser)
    Utils optionally (manager removeGroup testGroup)
    Utils optionally (manager removeGroup testGroup2)
    Utils optionally (manager removeGroup testGroup3)
  }

  test("server is reachable") {
    manager.users
  }
  test("user add/remove") {
    val usersBefore = manager.users
    manager addUser(testUser, testPassword)

    intercept[Exception] {
      // NameAlreadyBoundException for LDAP
      // MySQLIntegrityConstraintViolationException (SQLException) for MySQL
      manager addUser(testUser, testPassword)
    }
    assert(manager.users contains testUser)
    manager removeUser testUser
    assert(usersBefore === manager.users)
  }
  test("group add/remove") {
    val groupsBefore = manager.groups
    manager addGroup testGroup
    // group already exists
    intercept[Exception] {
      // NameAlreadyBoundException for LDAP
      // SQLException for MySQL
      manager addGroup testGroup
    }
    assert(manager.groups contains testGroup)
    manager removeGroup testGroup
    assert(groupsBefore === manager.groups)
  }
  test("remove non-empty group") {
    manager addGroup testGroup
    manager addUser(testUser, testPassword)
    manager assign(testUser, testGroup)
    intercept[Exception] {
      manager removeGroup testGroup
    }
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
  test("group setter") {
    manager addUser(testUser, testPassword)
    val aTeam = Seq(testGroup2, testGroup3)
    manager addGroups(testGroup2, testGroup3, testGroup)
    manager assign(testUser, testGroup)
    manager replaceGroups(testUser, aTeam)
    assert(manager.groups(testUser) === aTeam)
    aTeam foreach (g => manager revoke(testUser, g))
    manager removeGroups (aTeam: _*)
  }
}
