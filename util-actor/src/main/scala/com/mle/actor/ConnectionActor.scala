package com.mle.actor

import actors.Actor
import com.mle.util.Log
import com.mle.actor.Messages.{StringMessage, Stop}

/**
 *
 * @author Mle
 */
abstract class ConnectionActor[T](val address: T) extends Actor with Log {
  val onMessage: String => Unit

  def act() {
    loop {
      react {
        case StringMessage(msg) =>
          onMessage(msg)
        case Stop =>
          log info "Closing client"
          exit()
      }
    }
  }
}