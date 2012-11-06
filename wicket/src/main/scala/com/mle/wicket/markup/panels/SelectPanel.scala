package com.mle.wicket.markup.panels

import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.IModel
import java.util.{List => JList}
import org.apache.wicket.markup.html.form.{ListChoice, Form}
import com.mle.util.Log
import com.mle.wicket.model.{ReadOnlyModel, LDModel}
import com.mle.wicket.component.{SAjaxButton, EnabledToggle}
import org.apache.wicket.Component
import de.agilecoders.wicket.markup.html.bootstrap.common.NotificationPanel

/**
 *
 * @author mle
 */
abstract class SelectPanel[T](id: String, model: IModel[T], choices: IModel[JList[T]]) extends Panel(id) with Log {
  val selectForm = new Form("selectForm")
  add(selectForm)
  val feedbackPanel = new NotificationPanel("selectFeedback")
  val itemList = new ListChoice("choices", model, choices) {
    // TODO ajaxify
    override val wantOnSelectionChangedNotifications = true
  }
  val isSelected = ReadOnlyModel(choices.getObject contains model.getObject)
  val deleteButton = new SAjaxButton("delete", LDModel("Delete"))(target => {
    onDeleteClicked(model.getObject)
    feedbackPanel info "Deleted successfully"
    target add getPage
  }) with ActionButton
  val createNewButton = new SAjaxButton("create", LDModel("Create New"))(target => {
    onCreateNewSelected()
    target add getPage
  }) with ActionButton
  add(deleteButton, createNewButton)

  def selection = model.getObject

  trait ActionButton extends Component with EnabledToggle {
    def enabled = isSelected.getObject
  }

  def onDeleteClicked(deleteItem: T)

  def onCreateNewSelected()

  selectForm add(itemList, deleteButton, createNewButton, feedbackPanel)
}
