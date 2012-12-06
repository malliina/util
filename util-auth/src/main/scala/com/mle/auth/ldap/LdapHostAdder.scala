package com.mle.auth.ldap

import LdapHelper._
import LdapAttributes._

/**
 *
 * @author mle
 */
trait LdapHostAdder extends LdapManager {
  def groupInfo: GroupDnInfo

  def addHost(hostname: String, ip: String) {
    val hostAttrs = attributes(
      cn -> hostname,
      ipHostNumber -> ip,
      member -> ""
    )
    hostAttrs put attribute(objectClass, ipHost, groupOfNames)
    addEntry(groupInfo.toDN(hostname), hostAttrs)
  }

  def updateHost(hostname: String, ip: String) {
    val mods = updateModification(ipHostNumber -> ip)
    modifyEntry(groupInfo.toDN(hostname), mods)
  }
}
