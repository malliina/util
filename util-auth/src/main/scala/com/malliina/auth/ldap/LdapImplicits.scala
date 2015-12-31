package com.malliina.auth.ldap


/**
 *
 * @author mle
 */
object LdapImplicits {
  implicit def enum2str(e: LdapAttributes.Value): String = e.toString
}
