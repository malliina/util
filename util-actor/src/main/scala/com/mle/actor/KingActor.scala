package com.mle.actor

import com.mle.util.Log
import com.mle.actor.Messages.{Broadcast, Stop, StringMessage}
import akka.actor._

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
abstract class KingActor[T](messages: MessageTypes[T]) extends Actor with Log {
  private var connections = Set.empty[ActorBundle[T]]
  val clientActorBuilder: T => ActorRef

  def receive = {
    case messages.Connect(client) =>
      onConnect(client)
    case messages.Disconnect(client) =>
      onDisconnect(client)
    case Broadcast(msg) =>
      connections.foreach(_.actor ! StringMessage(msg))
    case Stop =>
      connections foreach (_.actor ! Stop)
      context.stop(self)
  }

  /**
   *
   * @param clientAddress client details
   * @return the number of connections
   */
  protected def onConnect(clientAddress: T): Int = {
    val clientActor = new ActorBundle(clientActorBuilder(clientAddress), clientAddress)
    connections += clientActor
    val conns = connections.size
    log info "Client connected. Open connections: " + conns
    conns
  }

  /**
   *
   * @param clientAddress
   * @return the number of remaining connections
   */
  protected def onDisconnect(clientAddress: T): Int = {
    connections.find(_.address == clientAddress).foreach(clientActor => {
      connections -= clientActor
      clientActor.actor ! Stop
    })
    val remainingConnections = connections.size
    log info "Client disconnected. Open connections: " + remainingConnections
    remainingConnections
  }

}

case class ActorBundle[T](actor: ActorRef, address: T)
