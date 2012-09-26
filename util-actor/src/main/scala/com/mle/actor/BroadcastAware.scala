package com.mle.actor


import com.mle.actor.Messages.{Start, Stop}
import actors.Actor

/**
 * @author Mle
 */
trait BroadcastAware[T] extends KingActor[T] {
  def broadcaster: Actor

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

