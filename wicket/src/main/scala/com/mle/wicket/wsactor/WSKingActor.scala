package com.mle.wicket.wsactor

import com.mle.wicket.wsactor.WsActors.Address
import com.mle.actor.KingActor

/**
 *
 * @author Mle
 */
class WSKingActor extends KingActor[Address] {
  val clientBuilder = new WSConnectionActor(_)
}
