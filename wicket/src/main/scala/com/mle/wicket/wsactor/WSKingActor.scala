package com.mle.wicket.wsactor

import com.mle.actor.{MessageTypes, Broadcaster, BroadcastAware, KingActor}
import com.mle.util.JsonUtils
import akka.actor.ActorDSL

/**
 * with BroadcastAware[Address]
 * @author Mle
 */
class WSKingActor extends KingActor[Address](new MessageTypes[Address]) {
  //  val broadcaster = new Broadcaster(this) {
  //    override def transformer = JsonUtils.toJson(_)
  //  }.start()
  val clientActorBuilder = (a: Address) => ActorDSL.actor(WsActors.system)(new WicketClient(a))
}
