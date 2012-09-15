package com.mle.actor

import com.mle.util.Util._
import com.mle.actor.Messages.Stop

/**
 *
 * @author Mle
 */
class ActorManager[T](val king: KingActor[T]) {
  king.start()
  addShutdownHook(king ! Stop)

  def connect(address: T) {
    king ! king.Connect(address)
  }

  def disconnect(address: T) {
    king ! king.Disconnect(address)
  }
}