package models

import com.mle.actor._
import controllers.WebSockets
import akka.actor.{ActorSystem, ActorRef, ActorDSL}
import com.mle.util.Util

/**
 *
 * @author Mle
 */

object WebSocketsManager extends ActorManager[WebSockets.ClientChannel] {
  private val system = ActorSystem("actor-system")
  Util.addShutdownHook(system.shutdown())

  val king = ActorDSL.actor(system)(new PlayKing(messages))

  class PlayKing(messages: MessageTypes[WebSockets.ClientChannel]) extends KingActor[WebSockets.ClientChannel](messages) {
    val clientActorBuilder = (channel: WebSockets.ClientChannel) => ActorDSL.actor(system)(new PlayClient(channel))
  }

}

class PlayClient(channel: WebSockets.ClientChannel)
  extends ConnectionActor[WebSockets.ClientChannel](channel) {
  val pushMessage: String => Unit = channel.push
}