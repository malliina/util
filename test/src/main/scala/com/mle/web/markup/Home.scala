package com.mle.web.markup

import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel
import collection.JavaConversions._
import com.mle.web.component.STab
import com.mle.util.Log
import com.mle.util.Util._
import org.apache.wicket.request.mapper.parameter.PageParameters

/**
 * TODO: Bookmarkable links to tabs under tabbedpanels
 * @author Mle
 *
 */

class Home(params: PageParameters) extends WebPage(params) with Log {
  val tab = params get "tab"
  log info "Tab: " + tab
  val tab1 = new STab("Tab 1", new Panel1(_))
  val tab2 = new STab("Tab 2", new Panel2(_))
  val tabs = new AjaxTabbedPanel("tabs", Seq(tab1, tab2))
  add(tabs)
  optionally(tab.toInt).foreach(tabNr => if (tabNr >= 0 && tabNr < tabs.size) tabs setSelectedTab tabNr)
}