package com.mle.wicket.wsactor

import com.mle.util.Log
import com.mle.actor.ActorManager

/**
 * @author Mle
 */

object WsActors extends ActorManager[Address](new WSKingActor) with Log {
  log info "Initializing Web Services actors"
}

case class Address(appName: String, sessionId: String, pageId: Int)

