package com.mle.wicket.wsactor

import com.mle.actor.ConnectionActor
import org.apache.wicket.Application
import org.apache.wicket.protocol.ws.api.SimpleWebSocketConnectionRegistry

/**
 *
 * @author Mle
 */
class WicketClient(address: Address) extends ConnectionActor[Address](address) {
  val appName = address.appName
  val session = address.sessionId
  val page = address.pageId
  val id = appName + "-" + session + "-" + page

  override val pushMessage: String => Unit = (msg) => {
    val registry = new SimpleWebSocketConnectionRegistry
    val conn = registry.getConnection(Application.get(appName), session, page)
    conn sendMessage msg
  }
}
