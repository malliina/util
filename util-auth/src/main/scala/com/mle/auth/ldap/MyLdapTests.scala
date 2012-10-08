package com.mle.auth.ldap

import com.mle.util.{FileUtilities, Log}
import javax.naming.directory.InitialDirContext

/**
 * @author Mle
 */
object MyLdapTests extends Log {
  def main(args: Array[String]) {
    val truststore = "conf/security/ca-cert.jks"
    val truststorePath = FileUtilities.pathTo(truststore).toAbsolutePath.toString
    log info "Truststore: " + truststorePath
    sys.props("javax.net.ssl.trustStore") = truststorePath
    sys.props("javax.net.ssl.trustStorePassword") = "eternal"
    val schema = LdapDirInfo("ldap://ubuntu.mle.com:389",
      DnInfo("cn", "dc=mle,dc=com"),
      DnInfo("uid", "ou=People,dc=mle,dc=com"),
      DnInfo("cn", "ou=Groups,dc=mle,dc=com")
    )
    val manager = LDAPUserManager(schema, "admin", "admin")
    //    log info "" + authMechanisms(schema.uri)
    //    testUserAddRemove(manager)
    testGroups(manager)
  }

  def authMechanisms(uri: String) = {
    val ctx = new InitialDirContext()
    ctx.getAttributes(uri, Array("supportedSASLMechanisms"))
  }

  def testGroups(manager: LDAPUserManager) {
    logSeq("Groups", manager.groups)
    manager.groups.foreach(manager.removeGroup)
    logSeq("Groups", manager.groups)
    manager.addGroup("admins")
    manager.addUser("john", "john")
    manager.assign("john", "admins")
    logSeq("Groups", manager.groups)
    logSeq("Users in group admins", manager.users("admins"))
    manager.revoke("john", "admins")
    logSeq("Users in group admins", manager.users("admins"))
    manager.assign("john", "admins")
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
