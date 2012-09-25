package com.mle.wicket

import markup.Pages.WebSocketsPage

/**
 * @author Mle
 */
class WebSocketsApplication extends BasicWebApplication {
  override def tabs = super.tabs :+ BootTab("Web Sockets", classOf[WebSocketsPage])
}
