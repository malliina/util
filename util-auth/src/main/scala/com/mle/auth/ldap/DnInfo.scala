package com.mle.auth.ldap

/**
 * @author Mle
 */
class DnInfo(val key: String, val branch: String) {
  def toDN(id: String) = key + "=" + id + "," + branch

  def toName(dn: String) = dn.stripPrefix(key + "=").stripSuffix("," + branch)
}