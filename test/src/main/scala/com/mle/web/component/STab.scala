package com.mle.web.component

import org.apache.wicket.model.{Model, IModel}
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab
import org.apache.wicket.markup.html.panel.Panel

/**
 * @author Mle
 */

class STab(title: IModel[String], panelBuilder: String => Panel) extends AbstractTab(title) {
  def this(title: String, panelBuilder: String => Panel) = this(Model.of(title), panelBuilder)

  def getPanel(panelId: String) = panelBuilder(panelId)
}