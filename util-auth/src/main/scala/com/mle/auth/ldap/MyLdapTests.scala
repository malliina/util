package com.mle.auth.ldap

import com.mle.util.Log

/**
 * @author Mle
 */
object MyLdapTests extends Log {
  def main(args: Array[String]) {
    val schema = LdapDirInfo("ldap://10.0.0.33:389",
      DnBuilder("cn", "dc=mle,dc=com"),
      DnBuilder("uid", "ou=People,dc=mle,dc=com"),
      DnBuilder("ou", "ou=Groups,dc=mle,dc=com")
    )
    val manager = LDAPUserManager(schema, "admin", "admin")
    manager.addUser("temp", "temp")
    manager.users foreach println
    manager.removeUser("temp")
    manager.users foreach println

    //    resource(johnsConnProvider.connection)(context => {
    //      val testAttrs = context.getAttributes(UserLdapAuthenticator.toDN(user))
    //      log info "Attrs: " + testAttrs.getAll.map(_.get()).mkString(", ")
    //      val searchControls = new SearchControls()
    //      searchControls setSearchScope SearchControls.SUBTREE_SCOPE
    //      val filter = "(&(objectClass=inetOrgPerson)(uid=john))"
    //      val returnedAttrs = Array("displayName")
    //      searchControls setReturningAttributes returnedAttrs
    //      val answer = context.search("dc=mle,dc=com", filter, searchControls)
    //      if (!answer.hasMore)
    //        log info "No results"
    //      val attrs = answer.flatMap(_.getAttributes.getAll.map(_.get()))
    //      log info "" + attrs.mkString(", ")
    //    })
  }
}
