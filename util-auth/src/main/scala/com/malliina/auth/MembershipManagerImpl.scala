package com.malliina.auth

import ldap.LdapAttributes._
import ldap.{LdapManager, DnInfo, GroupDnInfo}
import ldap.LdapHelper._
import javax.naming.directory.{SearchControls, DirContext}
import scala.Array
import collection.JavaConversions._

/**
 *
 * @author mle
 */
trait MembershipManagerImpl extends MembershipManager[String] with LdapManager {
  def userInfo: DnInfo

  def groupInfo: GroupDnInfo

  def assign(user: String, group: String) {
    modifyGroup(DirContext.ADD_ATTRIBUTE, user, group)
    //    if (existsUser(user))
    //      modifyGroup(DirContext.ADD_ATTRIBUTE, user, group)
    //    else
    //      throw new UserManagementException("User doesn't exist: " + user)
  }

  def revoke(user: String, group: String) {
    modifyGroup(DirContext.REMOVE_ATTRIBUTE, user, group)
  }

  def users(group: String) = {
    val attrs = connectionProvider.withConnection(_.getAttributes(groupInfo.toDN(group), Array(groupInfo.memberAttribute.toString)))
    // uniqueMember contains DNs of users
    Option(attrs get groupInfo.memberAttribute.toString)
      .map(_.getAll.map(dn => userInfo toName String.valueOf(dn)).filter(_.size > 0).toSeq)
      .getOrElse(Seq.empty)
  }

  def groups(user: String) = {
    val filter = "(&(" + objectClass + "=*)(" + groupInfo.memberAttribute + "=" + userInfo.toDN(user) + "))"
    val controls = new SearchControls()
    controls setSearchScope SearchControls.ONELEVEL_SCOPE
    val results = connectionProvider.withConnection(_.search(groupInfo.branch, filter, controls))
    results.map(group => groupInfo.toName(group.getName)).toSeq
  }

  //  def belongs(user: String, group: String) = users(group) contains user
  private def modifyGroup(modAttribute: Int, user: String, group: String) {
    val mods = arrayModification(modAttribute, groupInfo.memberAttribute -> userInfo.toDN((user)))
    modifyEntry(groupInfo.toDN(group), mods)
  }
}
