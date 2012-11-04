package com.mle.wicket.markup.panels

import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.IModel

/**
 *
 * @author mle
 */
abstract class EditPanel(id: String, editModel: IModel[String]) extends Panel(id, editModel) {
}
