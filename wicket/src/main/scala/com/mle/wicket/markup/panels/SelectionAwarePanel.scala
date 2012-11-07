package com.mle.wicket.markup.panels

import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.IModel

/**
 *
 * @author mle
 */
trait SelectionAwarePanel extends Panel {
  def isSelected: IModel[Boolean]
}
