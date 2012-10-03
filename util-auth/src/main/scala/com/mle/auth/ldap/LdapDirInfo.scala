package com.mle.auth.ldap

/**
 * @author Mle
 */
case class LdapDirInfo(uri: String, adminInfo: DnBuilder, usersInfo: DnBuilder, groupsInfo: DnBuilder)