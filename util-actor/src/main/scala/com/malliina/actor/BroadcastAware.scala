package com.malliina.actor

import akka.actor.ActorRef
import com.malliina.actor.Messages.{Start, Stop}
import com.malliina.util.Log

/**
  * @author Mle
  */
trait BroadcastAware[T] extends KingActor[T] with Log {
  def broadcaster: ActorRef

  override def onConnect(clientAddress: T) = {
    val conns = super.onConnect(clientAddress)
    broadcaster ! Start
    conns
  }

  override def onDisconnect(clientAddress: T) = {
    val conns = super.onDisconnect(clientAddress)
    if (conns == 0)
      broadcaster ! Stop
    conns
  }
}

