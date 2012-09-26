package com.mle.actor

/**
 *
 * @author Mle
 */
object Messages {

  case object Start

  case object Stop

  case class Broadcast(msg: String)

  case class StringMessage(msg: String)

  case class Msg[X](msg: X)

}
