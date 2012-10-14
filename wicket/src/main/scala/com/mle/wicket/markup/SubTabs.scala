package com.mle.wicket.markup

import de.agilecoders.wicket.markup.html.bootstrap.tabs.BootstrapTabbedPanel
import com.mle.wicket.component.STab
import collection.JavaConversions._

/**
 * @author Mle
 */
class SubTabs(id: String) extends TabsPanel(id) {
  def newTabbedPanel = new BootstrapTabbedPanel("tabs", STab("Sub Settings", new BootstrapPanel(_)) :: Nil)

  add(newTabbedPanel)
}
