package com.mle.auth.ldap

import com.mle.auth._
import exception.UserManagementException
import javax.naming.directory._
import collection.JavaConversions._
import javax.naming.Context
import com.mle.util.Implicits._
import com.mle.util.Util._
import com.mle.util.Log
import LdapAttributes._
import LdapImplicits._


/**
 * Abstract because there's no logging, so users are discouraged from using this directly.
 * Instead, add mixins to complete your perfect user manager.
 *
 * @author Mle
 */
abstract class AbstractLdapUserManager(val connectionProvider: LDAPConnectionProvider,
                                       val userInfo: DnInfo,
                                       groupInfo: DnInfo)
  extends CertUserManager
  with PasswordAuthenticator[String] with Log {

  def authenticate(user: String, password: String) = {
    val connectionProps = (connectionProvider.noUserProperties ++ Map(
      Context.SECURITY_PRINCIPAL -> userInfo.toDN(user),
      Context.SECURITY_CREDENTIALS -> password
    )).toProperties
    resource(new InitialDirContext(connectionProps))(conn => {})
    user
  }

  def addUser(user: String, password: String) {
    // add group
    addGroup(user)
    val gid = read(groupInfo.toDN(user), gidNumber)
    val objClassAttribute = attribute(objectClass, posixAccount, shadowAccount, inetOrgPerson)
    val userAttrs = attributes(
      uid -> user,
      uidNumber -> (maxUid + 1).toString,
      gidNumber -> gid,
      cn -> user,
      userPassword -> password,
      homeDirectory -> ("/home/" + user),
      loginShell -> "/bin/bash",
      // required if we use objectClass inetOrgPerson
      sn -> user
    )
    userAttrs put objClassAttribute
    addUser(user, userAttrs)
  }

  def addUser(user: String, attrs: BasicAttributes) {
    val dn = userInfo.toDN(user)
    connectionProvider.withConnection(_.bind(dn, null, attrs))
  }

  def removeUser(user: String) {
    val dn = userInfo.toDN(user)
    // We don't want removed user DNs to remain as group members
    groups(user).foreach(revoke(user, _))
    try {
      removeGroup(user)
    } catch {
      case e: Exception => log warn "Unable to remove user's group: " + user
    }
    connectionProvider.withConnection(_.unbind(dn))
  }

  def addGroup(group: String) {
    val groupAttrs = attributes(
      cn -> group,
      gidNumber -> (maxGid + 1).toString
    )
    val objClasses = attribute(objectClass, posixGroup)
    groupAttrs put objClasses
    val dn = groupInfo.toDN(group)
    connectionProvider.withConnection(_.bind(dn, null, groupAttrs))
  }

  def removeGroup(group: String) {
    val groupMembers = users(group)
    if (groupMembers.nonEmpty)
      throw new UserManagementException("Cannot remove non-empty group: " + group + ", members: " + groupMembers.mkString(", "))
    val dn = groupInfo.toDN(group)
    connectionProvider.withConnection(_.unbind(dn))
  }

  private def arrayModification(modAttribute: Int, kv: (String, String)) = {
    val (key, value) = kv
    Array(new ModificationItem(modAttribute, attributeStr(key, value)))
  }

  private def modifyGroup(modAttribute: Int, user: String, group: String) {
    val mod = arrayModification(modAttribute, memberuid.toString -> userInfo.toDN((user)))
    connectionProvider.withConnection(_.modifyAttributes(groupInfo.toDN(group), mod))
  }

  def assign(user: String, group: String) {
    if (existsUser(user))
      modifyGroup(DirContext.ADD_ATTRIBUTE, user, group)
    else
      throw new UserManagementException("User doesn't exist: " + user)
  }

  def revoke(user: String, group: String) {
    modifyGroup(DirContext.REMOVE_ATTRIBUTE, user, group)
  }

  def belongs(user: String, group: String) = users(group) contains user

  def groups(user: String) = {
    val filter = "(&(" + objectClass + "=" + posixGroup + ")(" + memberuid + "=" + userInfo.toDN(user) + "))"
    val controls = new SearchControls()
    controls setSearchScope SearchControls.ONELEVEL_SCOPE
    val results = connectionProvider.withConnection(_.search(groupInfo.branch, filter, controls))
    results.map(group => groupInfo.toName(group.getName)).toSeq
  }


  def uidOf(user: String) = read(userInfo.toDN(user), LdapAttributes.uidNumber).toInt

  def search(tree: String, attributeName: LdapAttributes.LdapAttribute) = {
    val filter = "(" + attributeName + "=*)"
    val controls = new SearchControls()
    controls setSearchScope SearchControls.ONELEVEL_SCOPE
    val results = connectionProvider.withConnection(_.search(tree, filter, controls))
    results.map(_.getAttributes.get(attributeName).get()).toSeq
  }

  def maxUid = {
    val uidNumbers = search(userInfo.branch, LdapAttributes.uidNumber).map(_.toString.toInt) :+ 10000
    uidNumbers.max
  }

  def maxGid = {
    val gidNumbers = search(groupInfo.branch, LdapAttributes.gidNumber).map(_.toString.toInt) :+ 5000
    gidNumbers.max
  }

  def users(group: String) = {
    val attrs = connectionProvider.withConnection(_.getAttributes(groupInfo.toDN(group), Array(memberuid.toString)))
    // uniqueMember contains DNs of users
    Option(attrs get memberuid.toString)
      .map(_.getAll.map(dn => userInfo toName String.valueOf(dn)).filter(_.size > 0).toSeq)
      .getOrElse(Seq.empty)
  }

  def read(dn: String, attribute: LdapAttributes.LdapAttribute) = {
    val attrStr = attribute.toString
    val attr = Array[String](attrStr)
    connectionProvider.withConnection(_.getAttributes(dn, attr).get(attrStr).get().toString)
  }

  def users = {
    //    val maxUid = read("cn=maxUid,dc=mle,dc=com", LdapAttributes.uidNumber).toInt
    //    println(maxUid)
    list(userInfo.branch, userInfo.key)
  }

  def groups = list(groupInfo.branch, groupInfo.key)

  private def list(branch: String, keyPrefix: String) = connectionProvider.withConnection(_.list(branch)
    .map(_.getName.stripPrefix(keyPrefix + "="))).toSeq

  def setPassword(user: String, newPassword: String) {
    val mod = arrayModification(DirContext.REPLACE_ATTRIBUTE, userPassword.toString -> newPassword)
    connectionProvider.withConnection(_.modifyAttributes(userInfo.toDN(user), mod))
  }

  private def attributesStr(keyValues: (String, String)*): BasicAttributes = {
    val attrs = new BasicAttributes()
    keyValues foreach (kv => {
      val (key, value) = kv
      attrs put new BasicAttribute(key, value)
    })
    attrs
  }

  private def attributes(keyValues: (LdapAttributes.LdapAttribute, String)*): BasicAttributes = {
    val stringified = keyValues.map(kv => kv._1.toString -> kv._2)
    attributesStr(stringified: _*)
  }

  private def attributeStr(attributeName: String, values: String*) = {
    val attr = new BasicAttribute(attributeName)
    values foreach attr.add
    attr
  }

  private def attribute2(attributeName: LdapAttributes.LdapAttribute,
                         value: String*): BasicAttribute = {
    attributeStr(attributeName.toString, value: _*)
  }

  private def attribute(attributeName: LdapAttributes.LdapAttribute,
                        value: LdapAttributes.LdapAttribute*): BasicAttribute = {
    attribute2(attributeName, value.map(_.toString): _*)
  }
}