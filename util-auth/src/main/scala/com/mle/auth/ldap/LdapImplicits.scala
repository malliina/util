package com.mle.auth.ldap


/**
 *
 * @author mle
 */
object LdapImplicits {
  implicit def enum2str(e: LdapAttributes.Value): String = e.toString
}
