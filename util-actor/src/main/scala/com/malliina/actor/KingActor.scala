package com.malliina.actor

import com.malliina.util.Log
import Messages.{Broadcast, Stop, StringMessage}
import akka.actor._

/**
 * A manager of actors.
 *
 * KingActors manage client connections/disconnections and can broadcast messages to all clients.
 *
 * Implementation note: We can't pattern match on generic containers so implementations should
 * override messageHandler to handle other than string messages, which is the default.
 *
 * @tparam T client address
 * @author Mle
 */
abstract class KingActor[T](messages: MessageTypes[T]) extends Actor with Log {
  protected var connections = Set.empty[ActorBundle[T]]
  val clientActorBuilder: T => ActorRef

  def managementHandler: Receive = {
    case messages.Connect(client) =>
      onConnect(client)
    case messages.Disconnect(client) =>
      onDisconnect(client)
    case Stop =>
      connections foreach (_.actor ! Stop)
      context.stop(self)
  }

  def messageHandler: Receive = {
    case Broadcast(msg) =>
      connections.foreach(_.actor ! StringMessage(msg))
  }

  def receive: Receive = messageHandler orElse managementHandler

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
