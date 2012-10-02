package com.mle.auth.ldap

import com.mle.util.Log
import javax.naming.directory.SearchControls
import collection.JavaConversions._
import com.mle.util.Util._

/**
 * @author Mle
 */
object LdapTest extends Log {
  def main(args: Array[String]) {
    val (user, pass) = ("john", "john")
    using(TestLdapConnectionProvider.login(user, pass))(context => {
      val testAttrs = context.getAttributes(TestLdapConnectionProvider.toDN(user))
      log info "Attrs: " + testAttrs.getAll.map(_.get()).mkString(", ")
      val searchControls = new SearchControls()
      searchControls setSearchScope SearchControls.SUBTREE_SCOPE
      val filter = "(&(objectClass=inetOrgPerson)(uid=john))"
      val returnedAttrs = Array("displayName")
      searchControls setReturningAttributes returnedAttrs
      val answer = context.search("dc=mle,dc=com", filter, searchControls)
      if (!answer.hasMore)
        log info "No results"
      val attrs = answer.flatMap(_.getAttributes.getAll.map(_.get()))
      log info "" + attrs.mkString(", ")
    })

  }
}
