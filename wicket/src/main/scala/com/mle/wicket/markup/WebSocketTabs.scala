package com.mle.wicket.markup

import de.agilecoders.wicket.markup.html.bootstrap.tabs.BootstrapTabbedPanel
import com.mle.wicket.component.STab
import collection.JavaConversions._

/**
 * @author Mle
 */
class WebSocketTabs(id: String) extends TabsPanel(id) {
  val simpleTab = STab("Simple", new WebSockets(_))
  val tableTab = STab("Table", new WebSocketsTablePanel(_))

  def newTabbedPanel = new BootstrapTabbedPanel("tabs", simpleTab :: tableTab :: Nil)

  add(newTabbedPanel)
}
