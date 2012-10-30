package com.mle.wicket

import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession
import org.apache.wicket.authroles.authorization.strategies.role.Roles
import org.apache.wicket.request.Request

/**
 *
 * @author mle
 */
class MyAuthenticatedWebSession(request: Request) extends AbstractAuthenticatedWebSession(request) {
  def getRoles = new Roles(Roles.ADMIN)

  def isSignedIn = false
}
