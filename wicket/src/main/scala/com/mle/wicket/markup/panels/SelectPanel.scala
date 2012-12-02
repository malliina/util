package com.mle.wicket.markup.panels

import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.IModel
import java.util.{List => JList}
import org.apache.wicket.markup.html.form.{ListChoice, Form}
import com.mle.util.Log
import com.mle.wicket.component.{SAjaxButton, EnabledToggle}
import org.apache.wicket.markup.html.basic.Label
import com.mle.web.wicket.model.{LDModel, ReadOnlyModel}

/**
 *
 * @author mle
 */
abstract class SelectPanel[T](id: String,
                              model: IModel[T],
                              choices: IModel[JList[T]],
                              header: String = "Select")
  extends Panel(id) with SelectionAwarePanel with Log {
  val selectForm = new Form("selectForm")
  add(selectForm)
  val isSelected = ReadOnlyModel(choices.getObject contains model.getObject)
  val headerLabel = new Label("header", header)
  val itemList = new ListChoice("choices", model, choices) {
    // TODO ajaxify
    override val wantOnSelectionChangedNotifications = true
  }
  val deleteButton = new MyAjaxButton("delete", LDModel("Delete"))(onDeleteClicked(model.getObject))
  val createNewButton = new MyAjaxButton("create", LDModel("Create New"))(onCreateNewSelected())

  selectForm add(headerLabel, itemList, deleteButton, createNewButton)

  def selection = model.getObject

  def onDeleteClicked(deleteItem: T)

  def onCreateNewSelected() {
    reset()
  }

  def reset()

  class MyAjaxButton(id: String, model: IModel[String])(onClick: => Unit) extends SAjaxButton(id, model)(target => {
    onClick
    target add getPage
  }) with EnabledToggle {
    def enabled = isSelected.getObject
  }

}
