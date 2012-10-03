package com.mle.auth.ldap

/**
 * @author Mle
 */
case class LdapSchema(uri: String, adminInfo: DnBuilder, usersInfo: DnBuilder, groupsInfo: DnBuilder)