package com.malliina.auth.ldap

/**
 *
 * @author mle
 */
case class GroupDnInfo(override val key: String,
                       override val branch: String,
                       memberAttribute: LdapAttributes.LdapAttribute)
  extends DnInfo(key, branch)