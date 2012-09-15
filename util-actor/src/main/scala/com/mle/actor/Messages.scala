package com.mle.actor

/**
 *
 * @author Mle
 */
object Messages {

  case object Stop

  case class StringMessage(msg: String)

  case class Msg[X](msg: X)

}
