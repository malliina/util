package com.mle.auth.tests

import org.scalatest.{BeforeAndAfter, FunSuite}
import com.mle.util.Util
import com.mle.auth.ldap.{SimpleLdapAuthenticator, LDAPUserManager}
import com.mle.auth.ldap.DnInfo
import com.mle.auth.ldap.LdapDirInfo
import javax.naming.{NameAlreadyBoundException, AuthenticationException}

/**
 * set fork := True when running from sbt
 *
 * @author mle
 */
class LdapTests extends FunSuite with BeforeAndAfter {
  private def setProperty(property: String, path: String) {
    val file = Util.resource(path).getFile
    sys.props(property) = file
    file
  }

  var manager: LDAPUserManager = null
  val testUser = "testUser"
  val testGroup = "testGroup"
  before {
    setProperty("javax.net.ssl.trustStore", "conf/security/ca-cert.jks")
    sys.props("javax.net.ssl.trustStorePassword") = "eternal"
    setProperty("javax.net.ssl.keyStore", "conf/security/client.jks")
    sys.props("javax.net.ssl.keyStorePassword") = "eternal"

    val ldapProps = Util.props("conf/security/auth.test")

    val schema = LdapDirInfo(ldapProps("ldap.uri"),
      DnInfo("cn", "dc=mle,dc=com"),
      DnInfo("uid", "ou=People,dc=mle,dc=com"),
      DnInfo("cn", "ou=Groups,dc=mle,dc=com")
    )
    manager = LDAPUserManager(schema, ldapProps("ldap.user"), ldapProps("ldap.pass"))
  }
  after {
    Util.optionally(manager removeUser testUser)
    Util.optionally(manager removeGroup testGroup)
  }

  test("LDAP server is reachable") {
    manager.groups
  }
  test("user add/remove") {
    val usersBefore = manager.users
    manager addUser(testUser, "temp")
    intercept[NameAlreadyBoundException] {
      manager addUser(testUser, "temp")
    }
    assert(manager.users contains testUser)
    manager removeUser testUser
    assert(usersBefore === manager.users)
  }
  test("group add/remove") {
    val groupsBefore = manager.groups
    manager addGroup testGroup
    intercept[NameAlreadyBoundException] {
      manager addGroup testGroup
    }
    assert(manager.groups contains testGroup)
    manager removeGroup testGroup
    assert(groupsBefore === manager.groups)
  }
  test("passwd changes") {
    val uri = manager.connectionProvider.authenticator.uri
    manager addUser(testUser, "miranda")
    val auth = new SimpleLdapAuthenticator(uri, manager.userInfo)
    auth authenticate(testUser, "miranda")
    manager setPassword(testUser, "temp")
    val thrown = intercept[AuthenticationException] {
      auth authenticate(testUser, "miranda")
    }
    assert(thrown.getMessage contains "Invalid Credentials")
    manager setPassword(testUser, "miranda")
    auth authenticate(testUser, "miranda")
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