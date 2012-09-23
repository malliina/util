package com.mle.wicket.markup

import com.mle.util.Log
import com.mle.wicket.component.SAjaxLink
import com.mle.wicket.wsactor.{Address, WsActors}
import org.apache.wicket.ajax.WebSocketRequestHandler
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.protocol.ws.api.message.{ClosedMessage, ConnectedMessage, TextMessage}
import org.apache.wicket.protocol.ws.api.{WicketWebSocketJQueryResourceReference, SimpleWebSocketConnectionRegistry, WebSocketBehavior}
import org.apache.wicket.markup.head.{JavaScriptHeaderItem, IHeaderResponse}
import org.apache.wicket.request.resource.PackageResourceReference
import collection.JavaConversions._

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
      val msg = WsActors.toJson("This message has been pushed as a response to an ajax request")
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

  override def renderHead(response: IHeaderResponse) {
    super.renderHead(response)
    response.render(JavaScriptHeaderItem.forReference(new ClientResourceReference))
  }

  private class ClientResourceReference extends PackageResourceReference(classOf[Home], "client.js") {
    override def getDependencies = Seq(JavaScriptHeaderItem.forReference(WicketWebSocketJQueryResourceReference.get()))
  }

}