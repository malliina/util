package com.mle.wicket.markup

import de.agilecoders.wicket.markup.html.bootstrap.tabs.BootstrapTabbedPanel
import org.apache.wicket.markup.html.WebPage
import com.mle.wicket.component.STab
import collection.JavaConversions._
import org.apache.wicket.markup.head.IHeaderResponse
import de.agilecoders.wicket.Bootstrap
import org.apache.wicket.extensions.markup.html.tabs.PanelCachingTab

/**
 * @author Mle
 */
class BootstrapPanelPage extends WebPage {
  val tabs = Seq(
    new STab("Home", new Home(_)),
    new PanelCachingTab(new STab("Settings", new Settings(_))),
    new STab("Web Sockets", new WebSockets(_)),
    new STab("Sorting", new SortPanel(_)),
    new STab("MOTD", new MessagePanel(_))
  )
  val tabbedPanel = new BootstrapTabbedPanel("tabs", tabs)
  add(tabbedPanel)

  override def renderHead(response: IHeaderResponse) {
    super.renderHead(response)
    Bootstrap.renderHead(response)
  }
}
