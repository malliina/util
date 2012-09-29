package com.mle.wicket.markup

import collection.JavaConversions._
import com.mle.util.Log
import com.mle.wicket.component.STab
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel
import org.apache.wicket.markup.html.panel.Panel

/**
 * TODO: Bookmarkable links to tabs under tabbedpanels
 * @author Mle
 *
 */

class Home(id: String) extends Panel(id) with Log {
  val wsTab = new STab("Web Sockets", new WebSockets(_))
  val atmosphereTab = new STab("Atmosphere", new Atmosphere(_))
  val sortTab = new STab("Sorting", new SortPanel(_))
  val settingsTab = new STab("Settings", new Settings(_))
  val tabs = Seq(sortTab, atmosphereTab, wsTab, settingsTab)
  val navigationPanel = new TabbedPanel("tabs", tabs)
  add(navigationPanel)
  //  optionally(tab.toString.toInt).foreach(tabNr =>
  //    if (tabNr >= 0 && tabNr < tabs.size) {
  //      navigationPanel setSelectedTab tabNr
  //    })
}