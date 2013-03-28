package com.mle.playutil.controllers

import org.apache.commons.codec.binary.Base64
import play.api.mvc._
import play.api.mvc.Controller


/**
 * @author Michael
 */
trait BasicAuth extends Controller {
  /**
   * Basic HTTP authentication.
   *
   * The "Authorization" request header should be like: "Basic base64(username:password)", where
   * base64(x) means x base64-encoded.
   *
   * @param request request from which the Authorization header is validated
   * @return the username wrapped in an Option if successfully authenticated, None otherwise
   */
  protected def authenticateFromHeader(implicit request: RequestHeader) = {
    request.headers.get(AUTHORIZATION).flatMap(authInfo => {
      authInfo.split(" ") match {
        case Array(authMethod, encodedCredentials) =>
          new String(Base64.decodeBase64(encodedCredentials.getBytes)).split(":", 2) match {
            case Array(user, pass) if authenticate(user, pass) => Some(user)
            case _ => None
          }
        case _ => None
      }
    })
  }

  /**
   *
   * @param username
   * @param password
   * @return true if the supplied credentials are valid, false otherwise
   */
  def authenticate(username: String, password: String): Boolean
}
