package com.mle.wicket.markup.panels

import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.IModel
import java.util.{List => JList}
import org.apache.wicket.markup.html.form.{Form, DropDownChoice}
import com.mle.util.Log
import com.mle.wicket.model.LDModel
import com.mle.wicket.component.EnabledToggle
import org.apache.wicket.Component
import org.apache.wicket.ajax.markup.html.form.AjaxButton
import org.apache.wicket.ajax.AjaxRequestTarget
import de.agilecoders.wicket.markup.html.bootstrap.common.NotificationPanel

/**
 *
 * @author mle
 */
abstract class SelectPanel(id: String, model: IModel[String], choices: IModel[JList[String]]) extends Panel(id) with Log {
  val selectForm = new Form("selectForm")
  add(selectForm)
  val feedbackPanel = new NotificationPanel("selectFeedback")
  val userList = new DropDownChoice("choices", model, choices) {
    override val wantOnSelectionChangedNotifications = true
    setNullValid(true)
  }
  val deleteButton = new AjaxButton("delete", LDModel("Delete")) with ActionButton {
    override def onSubmit(target: AjaxRequestTarget, form: Form[_]) {
      onDelete()
      feedbackPanel info "Deleted: " + selection
      target add getPage
    }
  }
  val createNewButton = new AjaxButton("create", LDModel("Create New")) with ActionButton {
    override def onSubmit(target: AjaxRequestTarget, form: Form[_]) {
      onCreateNewSelected()
      target add getPage
    }
  }
  add(deleteButton, createNewButton)

  def selection = model.getObject

  trait ActionButton extends Component with EnabledToggle {
    def enabled = selection != null && !selection.isEmpty
  }

  def onDelete()

  def onCreateNewSelected()

  selectForm add(userList, deleteButton, createNewButton, feedbackPanel)
}
