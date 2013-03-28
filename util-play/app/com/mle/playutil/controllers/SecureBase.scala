package com.mle.playutil.controllers

import play.api.mvc._

/**
 *
 * @author mle
 */
trait SecureBase extends BaseController with BasicAuth {
  /**
   * Called when an unauthorized HTTP request is made.
   */
  protected def onUnauthorized(implicit request: RequestHeader): Result
  /**
   * Retrieve the connected username.
   *
   * Attempt to read the "username" session variable, or if no such thing exists,
   * attempt to authenticate based on the the HTTP Authorization header.
   *
   * @return the username wrapped in an Option if successfully authenticated, None otherwise
   */
  def username(implicit request: RequestHeader) = {
    authenticateFromSession orElse authenticateFromHeader
  }
  protected def authenticateFromSession(implicit request: RequestHeader) =
    request.session.get("username")
  /**
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) =
    Authenticated(user => Action(request => f(user)(request)))

  def Authenticated(action: => EssentialAction): EssentialAction =
    Authenticated((s: String) => action)

  def Authenticated(usernameAction: String => EssentialAction): EssentialAction =
    Security.Authenticated(implicit req => username, implicit req => onUnauthorized)(usernameAction)

  def AuthResult(result: => Result) =
    IsAuthenticated(_ => _ => result)

  def AuthAction(block: Request[AnyContent] => Result) = IsAuthenticated(user => block)

  def AuthResult(result: (String, Request[AnyContent]) => Result) = IsAuthenticated {
    username => request => result(username, request)
  }

  def AuthenticatedAsync(result: => String => Request[AnyContent] => Result) =
    IsAuthenticated(user => implicit request => {
      AsyncFuture(result(user)(request))
    })

}