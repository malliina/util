package com.mle.auth.ldap

import com.mle.util.Log

/**
 * @author Mle
 */
object MyLdapTests extends Log {
  def main(args: Array[String]) {
    val schema = LdapDirInfo("ldap://10.0.0.33:389",
      DnInfo("cn", "dc=mle,dc=com"),
      DnInfo("uid", "ou=People,dc=mle,dc=com"),
      DnInfo("cn", "ou=Groups,dc=mle,dc=com")
    )
    val manager = LDAPUserManager(schema, "admin", "admin")
    //    testUserAddRemove(manager)
    testGroups(manager)
  }

  def testGroups(manager: LDAPUserManager) {
    logSeq("Groups", manager.groups)
    manager.groups.foreach(manager.removeGroup)
    logSeq("Groups", manager.groups)
    manager.addGroup("admins")
    manager.assign("john", "admins")
    logSeq("Groups", manager.groups)
    logSeq("Users in group admins", manager.users("admins"))
    manager.revoke("john", "admins")
    logSeq("Users in group admins", manager.users("admins"))
    manager.assign("john", "admins")
    logSeq("Users in group admins", manager.users("admins"))
    manager.removeUser("john")
    logSeq("Users in group admins", manager.users("admins"))
  }

  def testUserAddRemove(manager: LDAPUserManager) {
    val usersBefore = manager.users
    manager.addUser("temp", "temp")
    logSeq("Users", manager.users)
    manager.removeUser("temp")
    val usersAfter = manager.users
    logSeq("Users ", manager.users)
    assert(usersBefore == usersAfter)
  }

  def logSeq[T](title: String, items: Seq[T]) {
    log info title + ": " + items.mkString(", ")
  }
}
