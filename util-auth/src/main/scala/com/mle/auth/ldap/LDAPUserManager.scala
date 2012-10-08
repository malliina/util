package com.mle.auth.ldap

import com.mle.auth.UserManager
import javax.naming.directory._
import com.mle.util.Util._
import collection.JavaConversions._


/**
 * @author Mle
 */
class LDAPUserManager(connectionProvider: LDAPConnectionProvider, userInfo: DnInfo, groupInfo: DnInfo)
  extends UserManager {
  val groupMemberClass = "groupOfUniqueNames"
  val memberAttribute = "uniqueMember"

  def withContext[T](code: InitialDirContext => T) = resource(connectionProvider.connection)(code)

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

  /**
   *
   * @param user
   * @param password
   * @throws NameAlreadyBoundException
   */
  override def addUser(user: String, password: String) {
    // inetOrgPerson requires sn,cn
    val userAttrs = attributes(
      "uid" -> user,
      "userPassword" -> password,
      "objectClass" -> "inetOrgPerson",
      "sn" -> user,
      "cn" -> user
    )
    val dn = userInfo.toDN(user)
    withContext(_.bind(dn, null, userAttrs))
  }

  override def removeUser(user: String) {
    val dn = userInfo.toDN(user)
    // We don't want removed user DNs to remain as group members
    groups(user).foreach(revoke(user, _))
    withContext(_.unbind(dn))
  }

  override def addGroup(group: String) {
    val groupAttrs = attributes("cn" -> group, memberAttribute -> "")
    val objClasses = attribute("objectClass", groupMemberClass)
    groupAttrs put objClasses
    val dn = groupInfo.toDN(group)
    withContext(_.bind(dn, null, groupAttrs))
  }

  override def removeGroup(group: String) {
    val dn = groupInfo.toDN(group)
    withContext(_.unbind(dn))
  }

  private def modifyGroup(modAttribute: Int, user: String, group: String) {
    val mods = Array(new ModificationItem(modAttribute, attribute(memberAttribute, userInfo.toDN(user))))
    withContext(_.modifyAttributes(groupInfo.toDN(group), mods))
  }

  override def assign(user: String, group: String) {
    if (existsUser(user))
      modifyGroup(DirContext.ADD_ATTRIBUTE, user, group)
    else
      throw new Exception("User doesn't exist: " + user)
  }

  override def revoke(user: String, group: String) {
    modifyGroup(DirContext.REMOVE_ATTRIBUTE, user, group)
  }

  def belongs(user: String, group: String) = users(group) contains user

  def groups(user: String) = {
    val filter = "(&(objectClass=" + groupMemberClass + ")(" + memberAttribute + "=" + userInfo.toDN(user) + "))"
    val controls = new SearchControls()
    controls setSearchScope SearchControls.ONELEVEL_SCOPE
    val results = withContext(_.search(groupInfo.branch, filter, controls))
    results.map(group => groupInfo.toName(group.getName)).toSeq
  }

  def users(group: String) = {
    val attrs = withContext(_.getAttributes(groupInfo.toDN(group), Array(memberAttribute)))
    // uniqueMember contains DNs of users
    Option(attrs get memberAttribute)
      .map(_.getAll.map(dn => userInfo toName String.valueOf(dn)).filter(_.size > 0).toSeq)
      .getOrElse(Seq.empty)
  }

  def existsUser(user: String) = users contains user

  def users = list(userInfo.branch, userInfo.key)

  def groups = list(groupInfo.branch, groupInfo.key)

  private def list(branch: String, keyPrefix: String) = withContext(_.list(branch)
    .map(_.getName.stripPrefix(keyPrefix + "="))).toSeq
}

object LDAPUserManager {
  def apply(schema: LdapDirInfo,
            adminUser: String,
            adminPassword: String) = {
    val connProvider = new LDAPConnectionProvider {
      val user = adminUser

      val password = adminPassword

      val authenticator = new SimpleLdapAuthenticator(schema.uri, schema.adminInfo)
    }
    new LoggingLdapUserManager(connProvider, schema.usersInfo, schema.groupsInfo)
  }
}
