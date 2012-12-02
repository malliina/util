package com.mle.wicket.markup.panels

import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.IModel
import com.mle.wicket.component.SAjaxButton
import com.mle.util.Log
import org.apache.wicket.markup.html.basic.Label
import com.mle.web.wicket.model.ReadOnlyModel

/**
 *
 * @author mle
 */
abstract class UpdateAwareEditPanel[T](id: String,
                                       editModel: IModel[T],
                                       updating: IModel[Boolean])
  extends Panel(id, editModel) with Log {
  def item = editModel.getObject

  def isUpdating = updating.getObject

  val editModeModel = ReadOnlyModel(if (isUpdating) "Update" else "Create")
  val header = ReadOnlyModel(if (isUpdating) "Edit" else "Create New")
  val headerLabel = new Label("header", header)

  val submitButton = new SAjaxButton("submitButton", editModeModel)(target => {
    if (isUpdating) {
      onUpdate(item)
    } else {
      onCreate(item)
    }
    target add getPage
  })

  def onCreate(newItem: T)

  def onUpdate(updatedItem: T)
}
