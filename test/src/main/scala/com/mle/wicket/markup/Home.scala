package com.mle.wicket.markup

import collection.JavaConversions._
import com.mle.util.Log
import com.mle.util.Util._
import com.mle.wicket.component.STab
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.request.mapper.parameter.PageParameters

/**
 * TODO: Bookmarkable links to tabs under tabbedpanels
 * @author Mle
 *
 */

class Home(params: PageParameters) extends WebPage(params) with Log {
  val tab = params get "tab"
  val wsTab = new STab("Web Sockets", new WebSocketsPanel(_))
  val atmosphereTab = new STab("Atmosphere", new Atmosphere(_))
  val sortTab = new STab("Sorting", new SortPanel(_))
  val settingsTab = new STab("Settings", new Settings(_))
  val tabs = new AjaxTabbedPanel("tabs", Seq(atmosphereTab, sortTab, settingsTab))
  add(tabs)
  optionally(tab.toInt).foreach(tabNr => if (tabNr >= 0 && tabNr < tabs.size) tabs setSelectedTab tabNr)
}