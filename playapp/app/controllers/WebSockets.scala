package controllers

import play.api.libs.iteratee.{Concurrent, Iteratee}
import play.api.mvc._
import util.PlayLog
import models.WebSocketsManager


/**
 *
 * @author Mle
 */
object WebSockets extends Controller with PlayLog {
  type ClientChannel = Concurrent.Channel[String]

  def index = Action {
    implicit request => Ok(views.html.wsIndex("yo"))
  }

  def webSocket = WebSocket.using(request => {
    val (e, channel) = Concurrent.broadcast[String]
    onConnect(channel)
    val in = Iteratee.foreach[String](onMessage).mapDone(_ => onClose(channel))
    (in, e)
  })

  def onConnect(channel: ClientChannel) {
    WebSocketsManager connect channel

  }

  def onClose(channel: ClientChannel) {
    WebSocketsManager disconnect channel
  }

  def onMessage(msg: String) {
    log info "Server got msg: " + msg
  }
}
