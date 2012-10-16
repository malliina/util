package com.mle.jdbc.tests

import org.scalatest.{BeforeAndAfter, FunSuite}
import com.mle.auth.UserManager
import com.mle.util.Util

/**
 *
 * @author mle
 */
abstract class UserManagementTests extends FunSuite with BeforeAndAfter {
  val testUser = "testUser"
  val testGroup = "testGroup"

  def manager: UserManager

  after {
    Util.optionally(manager removeUser testUser)
    Util.optionally(manager removeGroup testGroup)
  }

  test("server is reachable") {
    manager.users
  }
  test("user add/remove") {
    val usersBefore = manager.users
    manager addUser(testUser, "temp")
    intercept[Exception] {
      // NameAlreadyBoundException for LDAP
      manager addUser(testUser, "temp")
    }
    assert(manager.users contains testUser)
    manager removeUser testUser
    assert(usersBefore === manager.users)
  }
  test("group add/remove") {
    val groupsBefore = manager.groups
    manager addGroup testGroup
    intercept[Exception] {
      // NameAlreadyBoundException for LDAP
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
    manager addUser(testUser, "john")
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
}
