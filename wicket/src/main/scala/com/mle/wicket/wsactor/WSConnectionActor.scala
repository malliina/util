package com.mle.wicket.wsactor

import com.mle.actor.ConnectionActor
import com.mle.wicket.wsactor.WsActors.Address
import org.apache.wicket.Application
import org.apache.wicket.protocol.ws.api.SimpleWebSocketConnectionRegistry

/**
 *
 * @author Mle
 */
class WSConnectionActor(address: Address) extends ConnectionActor[Address](address) {
  val appName = address.appName
  val session = address.sessionId
  val page = address.pageId
  val id = appName + "-" + session + "-" + page

  override val onMessage: String => Unit = (msg) => {
    val registry = new SimpleWebSocketConnectionRegistry
    val conn = registry.getConnection(Application.get(appName), session, page)
    conn sendMessage msg
  }
}
