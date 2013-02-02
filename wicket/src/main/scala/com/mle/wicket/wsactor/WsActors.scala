package com.mle.wicket.wsactor

import com.mle.util.Log
import com.mle.actor.ActorManager
import akka.actor.{ActorDSL, ActorSystem}

/**
 * @author Mle
 */

object WsActors extends ActorManager[Address] with Log {
  val system = ActorSystem("actor-system")

  log info "Initializing Web Services actors"
  val king = ActorDSL.actor(system)(new WSKingActor)
}

case class Address(appName: String, sessionId: String, pageId: Int)

