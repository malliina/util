package com.mle.auth.ldap

import javax.naming.directory.{ModificationItem, BasicAttributes}
import collection.JavaConversions._


/**
 *
 * @author mle
 */
trait LdapManager {
  def connectionProvider: LDAPConnectionProvider

  def addEntry(dn: String, attributes: BasicAttributes) {
    connectionProvider.withConnection(_.bind(dn, null, attributes))
  }

  def removeEntry(dn: String) {
    connectionProvider.withConnection(_.unbind(dn))
  }

  def modifyEntry(dn: String, modifications: Array[ModificationItem]) {
    connectionProvider.withConnection(_.modifyAttributes(dn, modifications))
  }

  def list(branch: String, keyPrefix: String) = connectionProvider.withConnection(_.list(branch)
    .map(_.getName.stripPrefix(keyPrefix + "="))).toSeq
}
