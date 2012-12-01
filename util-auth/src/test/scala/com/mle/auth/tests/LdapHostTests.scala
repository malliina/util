package com.mle.auth.tests

import org.scalatest.FunSuite
import com.mle.auth.ldap.{LdapAttributes, DefaultLdapUserManager}
import com.mle.auth.tests.LdapTestSettings._
import com.mle.util.Log
import com.mle.auth.ldap.UserHostAuthorizer.Host

/**
 *
 * @author mle
 */
class LdapHostTests extends FunSuite with Log {
  val hostManager = new DefaultLdapUserManager(connProvider, peopleInfo, hostInfo)
  test("can search host info") {
    val result = hostManager.searchMulti(
      hostManager.groupInfo.branch,
      LdapAttributes.cn,
      LdapAttributes.ipHostNumber
    )
    val ipAddresses = result.map(res => Host(res(LdapAttributes.cn).toString, res(LdapAttributes.ipHostNumber).toString))
    log info "Hosts: " + ipAddresses.mkString(", ")
  }

}
