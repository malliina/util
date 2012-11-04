package com.mle.wicket.markup.panels

import org.apache.wicket.model.IModel
import org.apache.wicket.markup.html.form.{TextField, Form}
import com.mle.wicket.model.LDModel
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.panel.Fragment
import com.mle.util.Log
import org.apache.wicket.ajax.markup.html.form.AjaxButton
import org.apache.wicket.ajax.AjaxRequestTarget
import com.mle.db.DatabaseSettings

/**
 * TODO move parts to a generic superclass for editing arbitrary objects
 * @author mle
 */
class UserEditPanel(id: String, editModel: IModel[String])
  extends EditPanel(id, editModel) with Log {
  def userManager = DatabaseSettings.userManager

  def username = editModel.getObject

  val form = new Form("editForm")
  add(form)

  def editModeModel = LDModel(
    if (username != null && username.size > 0) {
      "Update"
    } else {
      "Create"
    }
  )

  def editMode = editModeModel.getObject

  //  val feedbackPanel = new NotificationPanel("feedback")
  val usernameField = new TextField("username", editModel)
  //    if (editMode == "Create")
  //      new EditUserFragment("username", "userText")
  //    else
  //      new ReadUserFragment("username", "userLabel")
  val submitText = editModeModel
  val submitButton = new AjaxButton("submitButton", submitText) {
    override def onSubmit(target: AjaxRequestTarget, form: Form[_]) {
      // TODO determine whether to update or create
      onCreate()
      target add getPage
    }
  }

  def onUpdate() {
    log info "Update: " + username
  }

  def onCreate() {
    log info "Create: " + username
    userManager.addUser(username, "")
    //    feedbackPanel info "Created user: " + username
  }

  form add(usernameField, submitButton)

  private class EditUserFragment(id: String, markupId: String)
    extends Fragment(id, markupId, UserEditPanel.this) {
    add(new TextField("username", editModel))
  }

  private class ReadUserFragment(id: String, markupId: String)
    extends Fragment(id, markupId, UserEditPanel.this) {
    add(new Label("username", editModel))
  }

}
