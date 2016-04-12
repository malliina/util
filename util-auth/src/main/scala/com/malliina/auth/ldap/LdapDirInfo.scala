package com.malliina.auth.ldap

/**
 * @author Mle
 */
case class LdapDirInfo(uri: String,
                       adminInfo: DnInfo,
                       usersInfo: DnInfo,
                       groupsInfo: GroupDnInfo)