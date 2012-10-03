package com.mle.auth.ldap

import com.mle.auth.UserManager
import javax.naming.directory.{InitialDirContext, BasicAttributes, BasicAttribute}
import com.mle.util.Util._
import com.mle.util.Log
import collection.JavaConversions._


/**
 * @author Mle
 */
class LDAPUserManager(connectionProvider: TempName, userInfo: DnBuilder, groupInfo: DnBuilder) extends UserManager with Log {
  def withContext[T](code: InitialDirContext => T) = resource(connectionProvider.connection)(code)

  def newAttrs(keyValues: (String, String)*) = {
    val attrs = new BasicAttributes()
    keyValues foreach (kv => {
      val (key, value) = kv
      attrs put new BasicAttribute(key, value)
    })
    attrs
  }

  def addUser(user: String, password: String) {
    // inetOrgPerson requires sn,cn
    val userAttrs = newAttrs(
      "uid" -> user,
      "userPassword" -> password,
      "objectClass" -> "inetOrgPerson",
      "sn" -> user,
      "cn" -> user
    )
    val dn = userInfo.toDN(user)
    withContext(_.bind(dn, null, userAttrs))
    log info "Added user: " + user + ", DN: " + dn
  }

  def removeUser(user: String) {
    val dn = userInfo.toDN(user)
    withContext(_.unbind(dn))
    log info "Removed user: " + user + ",DN: " + dn
  }

  def addGroup(group: String) {
    val groupAttrs = newAttrs(
      "objectClass" -> "posixGroup",
      "cn" -> group
    )
    val dn = groupInfo.toDN(group)
    withContext(_.bind(dn, null, groupAttrs))
    log info "Added group: " + group + ", DN: " + dn
  }

  def removeGroup(group: String) {
    val dn = groupInfo.toDN(group)
    withContext(_.unbind(dn))
    log info "Removed group: " + group + ", DN: " + dn
  }

  def assign(user: String, group: String) {}

  def revoke(user: String, group: String) {}

  def belongs(user: String, group: String) = false

  def groups(user: String) = null

  def users(group: String) = null

  def users = withContext(_.list(userInfo.branch).map(_.getName.stripPrefix(userInfo.key + "="))).toSeq
}

object LDAPUserManager {
  def apply(schema: LdapSchema,
            adminUser: String,
            adminPassword: String) = {
    val connProvider = new TempName {
      val user = adminUser

      val password = adminPassword

      val authenticator = new LDAPAuthenticator(schema.uri, schema.adminInfo)
    }
    new LDAPUserManager(connProvider, schema.usersInfo, schema.groupsInfo)
  }
}
