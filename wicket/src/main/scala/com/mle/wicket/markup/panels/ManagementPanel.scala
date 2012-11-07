package com.mle.wicket.markup.panels

import org.apache.wicket.markup.html.panel.Panel
import de.agilecoders.wicket.markup.html.bootstrap.common.NotificationPanel

/**
 *
 * @author mle
 */
abstract class ManagementPanel(id: String) extends Panel(id) {
  val feedbackPanel = new NotificationPanel("feedback")
  add(feedbackPanel)

  def selectPanel: String => SelectionAwarePanel

  def editPanel: String => Panel

  lazy val sPanel = selectPanel("selectPanel")
  lazy val ePanel = editPanel("editPanel")
}
