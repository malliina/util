package com.mle.wicket.wsactor

import WsActors._
import actors.Actor
import com.mle.util.Log

/**
 * @author Mle
 */
class KingActor extends Actor with Log {
  private var connections = Set.empty[ConnectionActor]

  def act() {
    loop {
      react {
        case Connect(a) =>
          connections += ConnectionActor.build(a)
          log info "Client connected, got " + connections.size + " connections"
        case Disconnect(a) =>
          connections.find(conn => conn.address == a).foreach(c => {
            connections -= c
            c ! Stop
          })
          log info "Client disconnected, " + connections.size + " clients left"
        case Broadcast(msg) =>
          connections.foreach(_ ! Message(msg))
        case Stop =>
          connections foreach (_ ! Stop)
          exit()
      }
    }
  }
}

object KingActor {
  def build() = {
    val a = new KingActor
    a.start()
    a
  }
}