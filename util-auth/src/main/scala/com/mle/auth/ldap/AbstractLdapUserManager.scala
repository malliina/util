package com.mle.auth.ldap

import com.mle.auth.{PasswordAuthenticator, UserManager}
import javax.naming.directory._
import collection.JavaConversions._
import com.mle.auth.crypto.PasswordHashing
import javax.naming.Context
import com.mle.util.Implicits._


/**
 * Abstract because there's no logging and no hashing of passwords, so users are discouraged from using this directly.
 * Instead, add mixins to complete your perfect user manager.
 *
 * @author Mle
 */
abstract class AbstractLdapUserManager(val connectionProvider: LDAPConnectionProvider,
                                       val userInfo: DnInfo,
                                       groupInfo: DnInfo)
  extends UserManager with PasswordAuthenticator[InitialDirContext] {
  val groupMemberClass = "groupOfUniqueNames"
  val memberAttribute = "uniqueMember"

  def authenticate(user: String, password: String) = {
    val connectionProps = (connectionProvider.noUserProperties ++ Map(
      Context.SECURITY_PRINCIPAL -> userInfo.toDN(user),
      Context.SECURITY_CREDENTIALS -> password
    )).toProperties
    new InitialDirContext(connectionProps)
  }

  private def attributes(keyValues: (String, String)*) = {
    val attrs = new BasicAttributes()
    keyValues foreach (kv => {
      val (key, value) = kv
      attrs put new BasicAttribute(key, value)
    })
    attrs
  }

  private def attribute(attributeName: String, values: String*) = {
    val attr = new BasicAttribute(attributeName)
    values foreach attr.add
    attr
  }

  def addUser(user: String, password: String) {
    // inetOrgPerson requires sn,cn
    val userAttrs = attributes(
      "uid" -> user,
      "userPassword" -> password,
      "objectClass" -> "inetOrgPerson",
      "sn" -> user,
      "cn" -> user
    )
    val dn = userInfo.toDN(user)
    connectionProvider.withConnection(_.bind(dn, null, userAttrs))
  }

  def removeUser(user: String) {
    val dn = userInfo.toDN(user)
    // We don't want removed user DNs to remain as group members
    groups(user).foreach(revoke(user, _))
    connectionProvider.withConnection(_.unbind(dn))
  }

  def addGroup(group: String) {
    val groupAttrs = attributes("cn" -> group, memberAttribute -> "")
    val objClasses = attribute("objectClass", groupMemberClass)
    groupAttrs put objClasses
    val dn = groupInfo.toDN(group)
    connectionProvider.withConnection(_.bind(dn, null, groupAttrs))
  }

  def removeGroup(group: String) {
    val dn = groupInfo.toDN(group)
    connectionProvider.withConnection(_.unbind(dn))
  }

  private def arrayModification(modAttribute: Int, kv: (String, String)) = {
    val (key, value) = kv
    Array(new ModificationItem(modAttribute, attribute(key, value)))
  }

  private def modifyGroup(modAttribute: Int, user: String, group: String) {
    val mod = arrayModification(modAttribute, memberAttribute -> userInfo.toDN((user)))
    connectionProvider.withConnection(_.modifyAttributes(groupInfo.toDN(group), mod))
  }

  def assign(user: String, group: String) {
    if (existsUser(user))
      modifyGroup(DirContext.ADD_ATTRIBUTE, user, group)
    else
      throw new Exception("User doesn't exist: " + user)
  }

  def revoke(user: String, group: String) {
    modifyGroup(DirContext.REMOVE_ATTRIBUTE, user, group)
  }

  def belongs(user: String, group: String) = users(group) contains user

  def groups(user: String) = {
    val filter = "(&(objectClass=" + groupMemberClass + ")(" + memberAttribute + "=" + userInfo.toDN(user) + "))"
    val controls = new SearchControls()
    controls setSearchScope SearchControls.ONELEVEL_SCOPE
    val results = connectionProvider.withConnection(_.search(groupInfo.branch, filter, controls))
    results.map(group => groupInfo.toName(group.getName)).toSeq
  }

  def users(group: String) = {
    val attrs = connectionProvider.withConnection(_.getAttributes(groupInfo.toDN(group), Array(memberAttribute)))
    // uniqueMember contains DNs of users
    Option(attrs get memberAttribute)
      .map(_.getAll.map(dn => userInfo toName String.valueOf(dn)).filter(_.size > 0).toSeq)
      .getOrElse(Seq.empty)
  }

  def existsUser(user: String) = users contains user

  def users = list(userInfo.branch, userInfo.key)

  def groups = list(groupInfo.branch, groupInfo.key)

  private def list(branch: String, keyPrefix: String) = connectionProvider.withConnection(_.list(branch)
    .map(_.getName.stripPrefix(keyPrefix + "="))).toSeq

  def setPassword(user: String, newPassword: String) {
    val mod = arrayModification(DirContext.REPLACE_ATTRIBUTE, "userPassword" -> newPassword)
    connectionProvider.withConnection(_.modifyAttributes(userInfo.toDN((user)), mod))
  }
}

object AbstractLdapUserManager {
  def apply(schema: LdapDirInfo,
            adminUser: String,
            adminPassword: String,
            logging: Boolean = true) = {
    val connProvider = new LDAPConnectionProvider(schema.uri, adminUser, Some(adminPassword), schema.adminInfo)
    if (logging)
      new DefaultLdapUserManager(connProvider, schema.usersInfo, schema.groupsInfo)
    else
      new AbstractLdapUserManager(connProvider, schema.usersInfo, schema.groupsInfo) with PasswordHashing[InitialDirContext]
  }
}
