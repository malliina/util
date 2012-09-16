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
    onConnect(channel, request)
    val in = Iteratee.foreach[String](onMessage(_, channel)).mapDone(_ => onClose(channel))
    (in, e)
  })

  def onConnect(channel: ClientChannel, request: RequestHeader) {
    WebSocketsManager connect channel
    log debug "IP: " + request.remoteAddress +
      "\nQuery string: " + request.queryString +
      "\nMethod: " + request.method +
      "\nPath: " + request.path +
      "\nURI: " + request.uri +
      "\nVersion: " + request.version +
      "\nSession: " + request.session
  }

  def onClose(channel: ClientChannel) {
    WebSocketsManager disconnect channel
  }

  def onMessage(msg: String, channel: ClientChannel) {
    log info "Server got msg: " + msg + " from: " + channel
    channel push "Welcome"
  }
}
