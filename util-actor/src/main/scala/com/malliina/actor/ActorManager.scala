package com.malliina.actor

import akka.actor.ActorRef
import com.malliina.actor.Messages.Stop
import com.malliina.util.Util._

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