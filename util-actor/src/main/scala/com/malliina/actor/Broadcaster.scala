package com.malliina.actor

import java.util.concurrent.ScheduledFuture

import akka.actor.{Actor, ActorRef}
import com.malliina.actor.Messages.{Broadcast, Start, Stop}
import com.malliina.util.Log
import com.malliina.util.Scheduling._

import scala.concurrent.duration.DurationInt

/**
  * @author Mle
  */
class Broadcaster(king: ActorRef) extends Actor with Log {
  private var broadcastCount = 0
  private var broadcastTask: Option[ScheduledFuture[_]] = None

  def transformer: String => String = msg => msg

  private def newBroadcastTask = every(3.seconds) {
    broadcastCount += 1
    val msg = transformer("Broadcast nr: " + broadcastCount)
    log info msg
    king ! Broadcast(msg)
  }

  private def isBroadcasting = !broadcastTask.map(_.isDone).getOrElse(true)

  def receive = {
    case Start =>
      if (!isBroadcasting) {
        broadcastTask = Some(newBroadcastTask)
        log info "Started broadcast"
      }
    case Stop =>
      broadcastTask.foreach(_.cancel(true))
      log info "Stopped broadcast"
  }
}