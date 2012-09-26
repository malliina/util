package com.mle.wicket.wsactor

import com.mle.actor.{Broadcaster, BroadcastAware, KingActor}
import com.mle.util.JsonUtils

/**
 *
 * @author Mle
 */
class WSKingActor extends KingActor[Address] with BroadcastAware[Address] {
  val broadcaster = new Broadcaster(this) {
    override def transformer = JsonUtils.toJson(_)
  }.start()
  val clientBuilder = new WicketClient(_)
}
