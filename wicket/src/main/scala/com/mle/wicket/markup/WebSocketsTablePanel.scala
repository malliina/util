package com.mle.wicket.markup

import com.mle.util.{JsonUtils, Log}
import com.mle.wicket.component.SAjaxLink
import com.mle.wicket.wsactor.{Address, WsActors}
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.protocol.ws.api.message.{ClosedMessage, ConnectedMessage, TextMessage}
import org.apache.wicket.protocol.ws.api.{WebSocketRequestHandler, WicketWebSocketJQueryResourceReference, SimpleWebSocketConnectionRegistry, WebSocketBehavior}
import org.apache.wicket.markup.head.{JavaScriptHeaderItem, IHeaderResponse}
import org.apache.wicket.request.resource.PackageResourceReference
import collection.JavaConversions._
import com.mle.actor.Messages.Broadcast

/**
 * @author Mle
 */

class WebSocketsTablePanel(id: String) extends Panel(id) with Log {
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
      val messageText: Seq[Char] = message.getText
      log info "Got message: " + messageText

      messageText match {
        case Seq('B', msg@_*) =>
          // Broadcast
          //          val srcIP = RequestCycle.get().getRequest.asInstanceOf[WebSocketRequest].getContainerRequest.asInstanceOf[HttpServletRequest].getRemoteAddr
//          WsActors.king ! Broadcast(msg.toString())
        case Seq('U', msg@_*) =>
          // Unicast
          val pushMsg = "This is a reply to: " + msg
          handler push pushMsg
          log info "Pushed this response: " + pushMsg
        case anythingElse =>
          // Unknown message type
          log warn "Got unknown message type: " + anythingElse
      }
    }
  })

  override def renderHead(response: IHeaderResponse) {
    super.renderHead(response)
    response.render(JavaScriptHeaderItem.forReference(new ClientResourceReference))
  }

  private class ClientResourceReference extends PackageResourceReference(classOf[WebSocketsTablePanel], "websocketstable.js") {
    override def getDependencies = Seq(JavaScriptHeaderItem.forReference(WicketWebSocketJQueryResourceReference.get()))
  }

}