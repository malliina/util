package com.mle.actor

import com.mle.util.Log
import com.mle.actor.Messages.{StringMessage, Stop}
import akka.actor.Actor

/**
 *
 * @author Mle
 */
abstract class ConnectionActor[T](val address: T) extends Actor with Log {
  /**
   * Pushes the message to the client.
   *
   * Implementations can use the address to push the message.
   */
  val pushMessage: String => Unit

  def receive = {
    case StringMessage(msg) =>
      log debug "Client got message: " + msg
      pushMessage(msg)
    case Stop =>
      log debug "Client exiting"
      context.stop(self)
  }
}