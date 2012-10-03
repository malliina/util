package com.mle.auth.ldap

/**
 * @author Mle
 */
case class DnBuilder(key: String, branch: String) {
  def toDN(id: String) = key + "=" + id + "," + branch
}