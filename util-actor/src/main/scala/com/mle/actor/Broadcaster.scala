package com.mle.actor

import actors.Actor
import com.mle.util.Log
import java.util.concurrent.ScheduledFuture
import com.mle.util.Scheduling._
import com.mle.util.Implicits._

import scala.Some
import com.mle.actor.Messages.{Broadcast, Start, Stop}

/**
 * @author Mle
 */
class Broadcaster(king: KingActor[_]) extends Actor with Log {
  private var broadcastCount = 0
  private var broadcaster: Option[ScheduledFuture[_]] = None

  def transformer: String => String = msg => msg

  private def newBroadcaster = every(3.seconds) {
    broadcastCount += 1
    val msg = transformer("Broadcast nr: " + broadcastCount)
    log info msg
    king ! Broadcast(msg)
  }

  def act() {
    loop {
      react {
        case Start =>
          if (broadcaster.map(_.isDone).getOrElse(true)){
            broadcaster = Some(newBroadcaster)
            log info "Started broadcast"
          }
        case Stop =>
          broadcaster.foreach(_.cancel(true))
          log info "Stopped broadcast"
      }
    }
  }
}