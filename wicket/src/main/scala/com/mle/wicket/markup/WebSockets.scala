package com.mle.wicket.markup

import com.mle.util.Log
import com.mle.wicket.component.SAjaxLink
import com.mle.wicket.wsactor.WsActors
import com.mle.wicket.wsactor.WsActors.Address
import org.apache.wicket.ajax.WebSocketRequestHandler
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.protocol.ws.api.message.{ClosedMessage, ConnectedMessage, TextMessage}
import org.apache.wicket.protocol.ws.api.{SimpleWebSocketConnectionRegistry, WebSocketBehavior}

/**
 * @author Mle
 */

class WebSockets(id: String) extends Panel(id) with Log {
  val link = SAjaxLink("link")(target => {
    log info "Pressed link; pushing data to client..."
    val registry = new SimpleWebSocketConnectionRegistry
    val sessionId = getSession.getId
    val pageId = getPage.getPageId
    val maybeConn = Option(registry.getConnection(getApplication, sessionId, pageId))
    maybeConn.foreach(conn => {
      val handler = new WebSocketRequestHandler(this, conn)
      val msg = "This message has been pushed as a response to an ajax request"
      //      conn sendMessage msg
      handler push msg
      log info "Server pushed message: " + msg
    })
  })
  add(link)
  add(new WebSocketBehavior {
    def toAddress(msg: ConnectedMessage) = Address(msg.getApplication.getName, msg.getSessionId, msg.getPageId)

    override def onConnect(msg: ConnectedMessage) {
      log debug "Client connected, app: " + msg.getApplication + ", session: " + msg.getSessionId
      WsActors.connect(Address(msg.getApplication.getName, msg.getSessionId, msg.getPageId))
    }

    override def onClose(message: ClosedMessage) {
      log debug "Client connection closed"
      WsActors.disconnect(Address(message.getApplication.getName, message.getSessionId, message.getPageId))
    }

    override def onMessage(handler: WebSocketRequestHandler, message: TextMessage) {
      log info "Got message: " + message.getText
      val pushMsg = WsActors.toJson("This is a reply to: " + message.getText)
      handler push pushMsg
      log info "Pushed this response: " + pushMsg
    }
  })
}