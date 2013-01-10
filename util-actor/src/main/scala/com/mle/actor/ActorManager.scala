package com.mle.actor

import com.mle.util.Util._
import com.mle.actor.Messages.Stop
import akka.actor.ActorRef

/**
 *
 * @author Mle
 */
abstract class ActorManager[T] {
  val king: ActorRef
  val messages = new MessageTypes[T]

  addShutdownHook(king ! Stop)

  def connect(address: T) {
    king ! messages.Connect(address)
  }

  def disconnect(address: T) {
    king ! messages.Disconnect(address)
  }
}

class MessageTypes[T] {

  case class Connect(client: T)

  case class Disconnect(client: T)

}