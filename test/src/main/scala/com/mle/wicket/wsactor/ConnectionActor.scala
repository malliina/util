package com.mle.wicket.wsactor

import WsActors._
import actors.Actor
import com.mle.util.Log
import org.apache.wicket.Application
import org.apache.wicket.protocol.ws.api.SimpleWebSocketConnectionRegistry


/**
 * @author Mle
 */

class ConnectionActor(val address: Address) extends Actor with Log {
  val appName = address.appName
  val session = address.sessionId
  val page = address.pageId
  val id = appName + "-" + session + "-" + page

  def act() {
    loop {
      react {
        case Message(msg) =>
          val registry = new SimpleWebSocketConnectionRegistry
          val conn = registry.getConnection(Application.get(appName), session, page)
          conn sendMessage msg
        case Stop =>
          log info "Closing: " + id
          exit()
      }
    }
  }
}

object ConnectionActor {
  def build(a: Address) = {
    val client = new ConnectionActor(a)
    client.start()
    client
  }
}