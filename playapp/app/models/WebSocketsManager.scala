package models

import com.mle.actor.{ActorManager, ConnectionActor, KingActor}
import controllers.WebSockets
import com.mle.util.Scheduling._
import com.mle.util.Implicits._
import play.api.libs.iteratee.Concurrent

/**
 *
 * @author Mle
 */
object WebSocketsManager extends ActorManager(new King) {
  every(3.seconds) {
    king ! king.Broadcast("This is a broadcast")
  }

}

class King extends KingActor[WebSockets.ClientChannel] {
  val clientBuilder = new WsConnectionActor(_)
}

class WsConnectionActor(channel: WebSockets.ClientChannel)
  extends ConnectionActor[WebSockets.ClientChannel](channel) {
  val onMessage: String => Unit = channel.push
}