package com.mle.wicket.markup

import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.markup.html.panel.Panel
import com.mle.wicket.component.BootstrapNav

/**
 * A page that wraps a panel.
 *
 * I'm undecided on whether to construct Panels or Pages by default.
 *
 * For now I do panels and wrap them in a page using this class, when needed.
 *
 * @author Mle
 */
abstract class BootstrapPage(panelBuilder: String => Panel) extends WebPage with BootstrapNav {
  val panelId = "panel"
  add(panelBuilder(panelId))
}