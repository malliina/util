package com.mle.actor

import actors.Actor
import com.mle.util.Log
import com.mle.actor.Messages.{StringMessage, Stop}

/**
 * A manager of actors.
 *
 * KingActors can be thought of as servers that manage ConnectionActors which are clients.
 *
 * KingActors manage client connections/disconnections and can broadcast messages to all clients.
 *
 * @tparam T client address
 * @author Mle
 */
abstract class KingActor[T] extends Actor with Log {
  private var connections = Set.empty[ConnectionActor[T]]
  val clientBuilder: T => ConnectionActor[T]

  def act() {
    loop {
      react {
        case Connect(client) =>
          onConnect(client)
        case Disconnect(client) =>
          onDisconnect(client)
        case Broadcast(msg) =>
          connections.foreach(_ ! StringMessage(msg))
        case Stop =>
          connections foreach (_ ! Stop)
          exit()
      }
    }
  }

  def onConnect(clientAddress: T) {
    val clientActor = clientBuilder(clientAddress)
    clientActor.start()
    connections += clientActor
    log info "Client connected. Open connections: " + connections.size
  }

  def onDisconnect(clientAddress: T) {
    connections.find(_.address == clientAddress).foreach(clientActor => {
      connections -= clientActor
      clientActor ! Stop
    })
    log info "Client disconnected. Open connections: " + connections.size
  }

  case class Connect(client: T)

  case class Disconnect(client: T)

  case class Broadcast(msg: String)

}
