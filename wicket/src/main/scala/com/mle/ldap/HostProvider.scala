package com.mle.ldap

import com.mle.auth.ldap.{LdapAttributes, AbstractLdapUserManager}
import com.mle.wicket.markup.Hosts.Host

/**
 *
 * @author mle
 */
trait HostProvider extends AbstractLdapUserManager {
  def hosts = searchMulti(groupInfo.branch, LdapAttributes.cn, LdapAttributes.ipHostNumber)
    .map(res => Host(res(LdapAttributes.cn).toString, res(LdapAttributes.ipHostNumber).toString))
}
