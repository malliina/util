package com.mle.web.wsactor

import com.mle.util.Implicits._
import com.mle.util.Log
import com.mle.util.Scheduling._

/**
 * @author Mle
 */

object WsActors extends Log {
  val king = KingActor.build()
  private var i = 0

  every(3.seconds) {
    i += 1
    king ! Broadcast(i.toString)
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