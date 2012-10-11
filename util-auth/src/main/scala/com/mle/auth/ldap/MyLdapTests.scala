package com.mle.auth.ldap

import com.mle.util.{Util, Log}
import javax.naming.directory.InitialDirContext

/**
 * @author Mle
 */
object MyLdapTests extends Log {
  def setProperty(property: String, path: String) {
    val file = Util.resource(path).getFile
    sys.props(property) = file
    file
  }

  def main(args: Array[String]) {
    setProperty("javax.net.ssl.trustStore", "conf/security/ca-cert.jks")
    sys.props("javax.net.ssl.trustStorePassword") = "eternal"
    setProperty("javax.net.ssl.keyStore", "conf/security/client.jks")
    sys.props("javax.net.ssl.keyStorePassword") = "eternal"

    val schema = LdapDirInfo("ldaps://10.0.0.33:636",
      DnInfo("cn", "dc=mle,dc=com"),
      DnInfo("uid", "ou=People,dc=mle,dc=com"),
      DnInfo("cn", "ou=Groups,dc=mle,dc=com")
    )
    val manager = LDAPUserManager(schema, "admin", "admin")
    //    log info "" + authMechanisms(schema.uri)
    //    testUserAddRemove(manager)
    testGroups(manager)
    testPasswd(manager)
  }

  def authMechanisms(uri: String) = {
    val ctx = new InitialDirContext()
    ctx.getAttributes(uri, Array("supportedSASLMechanisms"))
  }

  def testPasswd(manager: LDAPUserManager) {
    val uri = manager.connectionProvider.authenticator.uri
    manager addUser("miranda", "miranda")
    val auth = new SimpleLdapAuthenticator(uri, manager.userInfo)
    auth authenticate("miranda", "miranda")
    manager setPassword("miranda", "temp")
    try {
      auth authenticate("miranda", "miranda")
    } catch {
      case e: Exception => log info "Auth failed with wrong password. Message: "+e.getMessage
    }
    manager setPassword("miranda", "miranda")
    auth authenticate("miranda", "miranda")
    manager removeUser ("miranda")
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
    manager.removeUser("john")
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
