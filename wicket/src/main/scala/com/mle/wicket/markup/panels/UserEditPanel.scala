package com.mle.wicket.markup.panels

import org.apache.wicket.model.IModel
import org.apache.wicket.markup.html.form.{ListMultipleChoice, PasswordTextField, TextField, Form}
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.panel.Fragment
import com.mle.util.Log
import com.mle.db.DatabaseSettings
import com.mle.wicket.model.{LDModel, ReadOnlyModel, RWModel}
import com.mle.wicket.markup.EditableUser
import java.util.{List => JList}
import collection.JavaConversions._

/**
 * @author mle
 */
class UserEditPanel(id: String, editModel: IModel[EditableUser])
  extends UpdateAwareEditPanel(id, editModel, ReadOnlyModel(editModel.getObject.newUser)) with Log {
  def userManager = DatabaseSettings.userManager

  val form = new Form("editForm")
  add(form)
  // http://technically.us/code/x/the-escape-hatch/
  val usernameField = new TextField("username", RWModel[String](item.username, item.username = _))
  val passwordField = new PasswordTextField("password",
    RWModel(item.password.getOrElse(""), pass => item.password = Some(pass))
  ).setRequired(false)
  val groupsModel: IModel[JList[String]] = LDModel(userManager.groups)
  val assignedGroups: IModel[JList[String]] = LDModel(item.groups)
  val groups = new ListMultipleChoice[String]("groups", assignedGroups, groupsModel)
  form add(usernameField, passwordField, groups, submitButton)

  def onUpdate(updatedUser: EditableUser) {
    log info "Update: " + item
    userManager setPassword(updatedUser.username, updatedUser.password.getOrElse(""))
  }

  def onCreate(newUser: EditableUser) {
    log info "Create: " + item
    userManager.addUser(newUser.username, newUser.password.getOrElse(""))
    newUser.groups.foreach(group => userManager.assign(newUser.username, group))
  }

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
