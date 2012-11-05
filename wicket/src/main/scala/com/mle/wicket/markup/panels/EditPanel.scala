package com.mle.wicket.markup.panels

import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.IModel
import com.mle.wicket.model.LDModel
import com.mle.wicket.component.SAjaxButton
import com.mle.util.Log

/**
 *
 * @author mle
 */
abstract class EditPanel[T](id: String, editModel: IModel[T], creating: IModel[Boolean])
  extends Panel(id, editModel) with Log {
  def item = editModel.getObject

  val editModeModel = LDModel(if (creating.getObject) "Create" else "Update")

  val submitButton = new SAjaxButton("submitButton", editModeModel)(target => {
    log info "Object: " + editModel.getObject + ", creating: " + creating.getObject
    if (creating.getObject) {
      onCreate(item)
    } else {
      onUpdate(item)
    }
    target add getPage
  })

  def onCreate(newItem: T)

  def onUpdate(updatedItem: T)
}
