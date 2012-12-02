package com.mle.web.wicket.markup

import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.markup.head.IHeaderResponse
import de.agilecoders.wicket.Bootstrap

/**
 *
 * @author mle
 */
abstract class PanelPage(panelBuilder: String => Panel) extends WebPage {
  val panelId = "panel"
  add(panelBuilder(panelId))

  override def renderHead(response: IHeaderResponse) {
    super.renderHead(response)
    Bootstrap.renderHead(response)
  }
}