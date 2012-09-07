package com.mle.web.markup

import collection.JavaConversions._
import com.mle.util.Log
import com.mle.web.component.SAjaxLink
import org.apache.wicket.Component
import org.apache.wicket.ajax.WebSocketRequestHandler
import org.apache.wicket.markup.head.{IHeaderResponse, JavaScriptHeaderItem}
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.protocol.ws.api.message.{ClosedMessage, ConnectedMessage, TextMessage}
import org.apache.wicket.protocol.ws.api.{WicketWebSocketJQueryResourceReference, SimpleWebSocketConnectionRegistry, WebSocketBehavior}
import org.apache.wicket.request.resource.PackageResourceReference

/**
 * @author Mle
 */

class WebSocketsPanel(id: String) extends Panel(id) with Log {
  val link = SAjaxLink("link")(target => {
    log info "Pressed link; pushing data to client..."
    val registry = new SimpleWebSocketConnectionRegistry
    val sessionId = getSession.getId
    val pageId = getPage.getPageId
    val maybeConn = Option(registry.getConnection(getApplication, sessionId, pageId))
    maybeConn.foreach(conn => {
//      conn sendMessage "Anybody home?"
      val handler = new WebSocketRequestHandler(this, conn)
      val msg = "This message has been pushed after an ajax request"
      handler push msg
      log info "Server pushed message: " + msg
    })
  })
  add(link)
  add(new WebSocketBehavior {
    override def onConnect(message: ConnectedMessage) {
      log info "Client connected, app: " + message.getApplication + ", session: " + message.getSessionId
    }

    override def onClose(message: ClosedMessage) {
      log info "Client connection closed"
    }

    override def onMessage(handler: WebSocketRequestHandler, message: TextMessage) {
      log info "Got message: " + message.getText
      val pushMsg = "This is a reply to: " + message.getText
      handler push pushMsg
      log info "Pushed this response: " + pushMsg
    }

    override def renderHead(component: Component, response: IHeaderResponse) {
      super.renderHead(component, response)
      response.render(JavaScriptHeaderItem.forReference(new ClientResourceReference))
    }

    private class ClientResourceReference extends PackageResourceReference(classOf[WebSocketsPanel], "client.js") {
      override def getDependencies = Seq(JavaScriptHeaderItem.forReference(WicketWebSocketJQueryResourceReference.get()))
    }

  })
}