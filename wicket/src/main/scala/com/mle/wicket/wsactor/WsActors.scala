package com.mle.wicket.wsactor

import com.mle.util.Implicits._
import com.mle.util.Log
import com.mle.util.Scheduling._
import com.mle.util.Util._

/**
 * @author Mle
 */

object WsActors extends Log {
  log info "Initializing Web Services actors"
  val JSON_FORMAT = """{"message": "%s", "version": %d}"""
  val king = KingActor.build()
  private var i = 0
  addShutdownHook(king ! Stop)
  every(1.seconds) {
    i += 1
    val msg = String.format(JSON_FORMAT, "Message nr: " + i, new Integer(4))
    log debug "Broadcasting: " + msg
    king ! Broadcast(msg)
  }

  case class Address(appName: String, sessionId: String, pageId: Int)

  case class Connect(address: Address)

  case class Disconnect(address: Address)

  case class Broadcast(msg: String)

  case class Message(msg: String)

  case object Stop

  def connect(address: Address) {
    king ! Connect(address)
  }

  def disconnect(address: Address) {
    king ! Disconnect(address)
  }
}