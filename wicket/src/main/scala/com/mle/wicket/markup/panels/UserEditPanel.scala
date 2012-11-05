package com.mle.wicket.markup.panels

import org.apache.wicket.model.{PropertyModel, IModel}
import org.apache.wicket.markup.html.form.{TextField, Form}
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.panel.Fragment
import com.mle.util.Log
import com.mle.db.DatabaseSettings
import com.mle.wicket.model.RWModel
import com.mle.wicket.markup.EditableUser

/**
 * @author mle
 */
class UserEditPanel(id: String, editModel: IModel[EditableUser])
  extends EditPanel(id, editModel, new PropertyModel[Boolean](editModel, "newUser")) with Log {
  def userManager = DatabaseSettings.userManager

  val form = new Form("editForm")
  add(form)
  val usernameField = new TextField("username", RWModel[String](editModel.getObject.username, editModel.getObject.username = _))


  def onUpdate(updatedUser: EditableUser) {
    log info "Update: " + item
  }

  def onCreate(newUser: EditableUser) {
    log info "Create: " + item
    userManager.addUser(newUser.username, "")
    newUser.groups.foreach(group => userManager.assign(newUser.username, group))
    //    feedbackPanel info "Created user: " + username
  }

  form add(usernameField, submitButton)

  // work in progress

  //  val feedbackPanel = new NotificationPanel("feedback")
  //    if (editMode == "Create")
  //      new EditUserFragment("username", "userText")
  //    else
  //      new ReadUserFragment("username", "userLabel")
  private class EditUserFragment(id: String, markupId: String)
    extends Fragment(id, markupId, UserEditPanel.this) {
    add(new TextField("username", editModel))
  }

  private class ReadUserFragment(id: String, markupId: String)
    extends Fragment(id, markupId, UserEditPanel.this) {
    add(new Label("username", editModel))
  }

}
