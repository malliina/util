package com.mle.wicket.markup.panels

import org.apache.wicket.markup.html.panel.Panel

/**
 *
 * @author mle
 */
abstract class ManagementPanel(id: String) extends Panel(id) {
  def selectPanel: String => Panel

  def editPanel: String => Panel

  lazy val sPanel = selectPanel("selectPanel")
  lazy val ePanel = editPanel("editPanel")
}
