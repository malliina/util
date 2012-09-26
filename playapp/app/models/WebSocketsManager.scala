package models

import com.mle.actor._
import controllers.WebSockets

/**
 *
 * @author Mle
 */
object WebSocketsManager extends ActorManager(new King)

class King extends KingActor[WebSockets.ClientChannel] with BroadcastAware[WebSockets.ClientChannel] {
  val clientBuilder = new PlayClient(_)

  val broadcaster = new Broadcaster(this).start()
}

class PlayClient(channel: WebSockets.ClientChannel)
  extends ConnectionActor[WebSockets.ClientChannel](channel) {
  val pushMessage: String => Unit = channel.push
}