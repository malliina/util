package com.mle.wicket.markup

import org.apache.wicket.markup.html.panel.Panel
import de.agilecoders.wicket.markup.html.bootstrap.tabs.BootstrapTabbedPanel
import com.mle.wicket.component.STab
import collection.JavaConversions._

/**
 * @author Mle
 */
class SubTabs(id: String) extends Panel(id) {
  val tabbedPanel = new BootstrapTabbedPanel("tabs", STab("Sub Settings", new Settings(_)) :: Nil)
  add(tabbedPanel)
}
