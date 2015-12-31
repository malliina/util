package com.malliina.auth.ldap

import java.util
import javax.naming.Context
import javax.naming.directory._

import com.malliina.auth._
import com.malliina.auth.ldap.LdapAttributes._
import com.malliina.auth.ldap.LdapHelper._
import com.malliina.auth.ldap.LdapImplicits._
import com.malliina.util.{Log, Utils}

import scala.collection.JavaConversions._


/**
 * Abstract because there's no logging, so users are discouraged from using this directly.
 * Instead, add mixins to complete your perfect user manager.
 *
 * @author Mle
 */
abstract class AbstractLdapUserManager(val connectionProvider: LDAPConnectionProvider,
                                       val userInfo: DnInfo,
                                       val groupInfo: GroupDnInfo)
  extends CertUserManager
  with PasswordAuthenticator[String]
  with LdapManager
  with MembershipManagerImpl
  with Log {

  def authenticate(user: String, password: String) = {
    import collection.JavaConverters._
    val map = connectionProvider.noUserProperties ++ Map(
      Context.SECURITY_PRINCIPAL -> userInfo.toDN(user),
      Context.SECURITY_CREDENTIALS -> password
    )
    val connectionProps = new util.Hashtable[String, String](map.asJava)
    Utils.resource(new InitialDirContext(connectionProps))(conn => {})
    user
  }

  def addUser(user: String, password: String) {
    // add group
    addGroup(user)
    val gid = read(groupInfo.toDN(user), gidNumber)
    val objClassAttribute = attribute(objectClass, posixAccount, shadowAccount, inetOrgPerson)
    // also assigns the user to the group referenced by gidNumber; i.e. the primary group
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

  def addUser(user: String, attributes: BasicAttributes) {
    addEntry(userInfo.toDN(user), attributes)
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
    removeEntry(dn)
  }

  def addGroup(group: String) {
    val groupAttrs = attributes(
      cn -> group,
      gidNumber -> (maxGid + 1).toString
    )
    val objClasses = attribute(objectClass, posixGroup)
    groupAttrs put objClasses
    addGroup(group, groupAttrs)
  }

  def addGroup(group: String, attributes: BasicAttributes) {
    addEntry(groupInfo.toDN(group), attributes)
  }

  def removeGroup(group: String) {
//    val groupMembers = users(group)
//    if (groupMembers.nonEmpty)
//      throw new UserManagementException("Cannot remove non-empty group: " + group + ", members: " + groupMembers.mkString(", "))
    removeEntry(groupInfo.toDN(group))
  }

  def uidOf(user: String) = read(userInfo.toDN(user), LdapAttributes.uidNumber).toInt

  def search(tree: String, attributeName: LdapAttributes.LdapAttribute) = {
    val filter = "(" + attributeName + "=*)"
    val controls = new SearchControls()
    controls setSearchScope SearchControls.ONELEVEL_SCOPE
    val results = connectionProvider.withConnection(_.search(tree, filter, controls))
    results.map(_.getAttributes.get(attributeName).get()).toSeq
  }

  def searchMulti(tree: String, attributeNames: LdapAttributes.LdapAttribute*) = {
    val filters = attributeNames.map(attr => "(" + attr + "=*)")
    val filter = "(&" + filters.mkString + ")"
    val controls = new SearchControls()
    controls setSearchScope SearchControls.ONELEVEL_SCOPE
    val results = connectionProvider.withConnection(_.search(tree, filter, controls))
    results.map(res => {
      val attrs = res.getAttributes
      attributeNames.map(attr => (attr -> attrs.get(attr).get)).toMap
    }).toSeq
  }

  def maxUid = {
    val uidNumbers = search(userInfo.branch, LdapAttributes.uidNumber).map(_.toString.toInt) :+ 10000
    uidNumbers.max
  }

  def maxGid = {
    val gidNumbers = search(groupInfo.branch, LdapAttributes.gidNumber).map(_.toString.toInt) :+ 5000
    gidNumbers.max
  }

  def read(dn: String, attribute: LdapAttributes.LdapAttribute) = {
    val attrStr = attribute.toString
    val attr = Array[String](attrStr)
    connectionProvider.withConnection(_.getAttributes(dn, attr).get(attrStr).get().toString)
  }

  def users = list(userInfo.branch, userInfo.key)

  def groups = list(groupInfo.branch, groupInfo.key)

  def setPassword(user: String, newPassword: String) {
    val mod = arrayModification(DirContext.REPLACE_ATTRIBUTE, userPassword -> newPassword)
    modifyEntry(userInfo.toDN(user), mod)
  }
}