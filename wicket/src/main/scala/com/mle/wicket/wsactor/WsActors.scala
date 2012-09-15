package com.mle.wicket.wsactor

import com.mle.util.Implicits._
import com.mle.util.Log
import com.mle.util.Scheduling._
import com.mle.actor.ActorManager

/**
 * @author Mle
 */

object WsActors extends ActorManager(new WSKingActor) with Log {
  log info "Initializing Web Services actors"
  val JSON_FORMAT = """{"message": "%s", "version": %d}"""
  private var i = 0
  every(1 seconds) {
    i += 1
    val msg = toJson("Message nr: " + i)
    log info "Broadcasting: " + msg
    king ! king.Broadcast(msg)
  }

  /**
   * Converts the parameters to a valid JSON string
   * @param msg the message
   * @param version a totally arbitrary number just to test json
   * @return the message in JSON format
   */
  def toJson(msg: String, version: java.lang.Integer = new Integer(4)) = String.format(JSON_FORMAT, msg, version)

  case class Address(appName: String, sessionId: String, pageId: Int)

}
