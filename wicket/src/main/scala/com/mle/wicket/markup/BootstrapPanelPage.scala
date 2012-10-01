package com.mle.wicket.markup

import org.apache.wicket.markup.html.WebPage
import com.mle.wicket.component.STab
import org.apache.wicket.markup.head.IHeaderResponse
import de.agilecoders.wicket.Bootstrap
import org.apache.wicket.extensions.markup.html.tabs.PanelCachingTab
import com.mle.wicket.component.bootstrap.LRBootstrapTabbedPanel

/**
 * @author Mle
 */
class BootstrapPanelPage extends WebPage {
  val leftTabs = STab("Home", new Home(_)) ::
    new PanelCachingTab(STab("Settings", new Settings(_))) ::
    STab("Web Sockets", new WebSockets(_)) :: Nil
  val rightTabs = STab("Sorting", new SortPanel(_)) :: STab("More Stuff", new SubTabs(_)) :: Nil
  val tabbedPanel = new LRBootstrapTabbedPanel("tabs", leftTabs, rightTabs)
  add(tabbedPanel)

  override def renderHead(response: IHeaderResponse) {
    super.renderHead(response)
    Bootstrap.renderHead(response)
  }
}
