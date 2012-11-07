package com.mle.wicket.markup.panels

import org.apache.wicket.model.IModel
import com.mle.db.DatabaseSettings
import org.apache.wicket.markup.html.form.{Form, TextField}
import com.mle.wicket.component.EnabledToggle

/**
 *
 * @author mle
 */
class GroupEditPanel(id: String, model: IModel[String], updating: IModel[Boolean])
  extends UpdateAwareEditPanel(id, model, updating) {
  def userManager = DatabaseSettings.userManager

  val form = new Form("editForm")
  add(form)

  val usernameField = new TextField("groupName", model) with EnabledToggle {
    def enabled = !updating.getObject
  }
  form add(usernameField, submitButton, headerLabel)

  def onCreate(newItem: String) {
    userManager addGroup newItem
    info("Added group: " + newItem)
  }

  def onUpdate(updatedItem: String) {
    info("Nothing to update")
  }
}
