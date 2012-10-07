package com.mle.auth.ldap

/**
 * @author Mle
 */
case class DnInfo(key: String, branch: String) {
  def toDN(id: String) = key + "=" + id + "," + branch

  def toName(dn: String) = dn.stripPrefix(key + "=").stripSuffix("," + branch)
}