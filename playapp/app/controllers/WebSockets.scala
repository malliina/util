package controllers

import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.mvc._
import util.PlayLog

/**
 *
 * @author Mle
 */
object WebSockets extends Controller with PlayLog {
  def index = WebSocket.using(request => {
    val in = Iteratee.foreach[String](str => log.info(str)).mapDone(_ => log info "Disconnected")
    val out = Enumerator("Hello, World!")
    (in, out)
  })

  def openClose = WebSocket.using(request => {
    val in = Iteratee.foreach[String](str => log info str).mapDone(_ => log info "Gone!")
    val out = Enumerator("Hello, Bye") >>> Enumerator.eof
    (in, out)
  })
}
