package com.mle.web.markup

import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel
import collection.JavaConversions._
import com.mle.web.component.STab

/**
 * TODO: Bookmarkable links to tabs under tabbedpanels
 * @author Mle
 *
 */

class Home extends WebPage {
  val tab1 = new STab("Tab 1", new Panel1(_))
  val tabs = new AjaxTabbedPanel("tabs", Seq(tab1))
  add(tabs)
}