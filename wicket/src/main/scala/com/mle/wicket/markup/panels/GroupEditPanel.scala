package com.mle.wicket.markup.panels

import org.apache.wicket.model.IModel
import org.apache.wicket.markup.html.form.{RequiredTextField, Form}
import com.mle.wicket.component.EnabledToggle
import com.mle.auth.UserManager

/**
 *
 * @author mle
 */
abstract class GroupEditPanel(id: String, model: IModel[String], updating: IModel[Boolean])
  extends UpdateAwareEditPanel(id, model, updating) {
  def userManager: UserManager[String]

  val form = new Form("editForm")
  add(form)

  val usernameField = new RequiredTextField("groupName", model) with EnabledToggle {
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
