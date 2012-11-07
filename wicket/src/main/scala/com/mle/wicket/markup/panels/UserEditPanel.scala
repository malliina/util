package com.mle.wicket.markup.panels

import org.apache.wicket.model.IModel
import org.apache.wicket.markup.html.form.{ListMultipleChoice, PasswordTextField, TextField, Form}
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.panel.Fragment
import com.mle.util.Log
import com.mle.db.DatabaseSettings
import com.mle.wicket.model.{LDModel, RWModel}
import com.mle.wicket.markup.EditableUser
import java.util.{List => JList, ArrayList => JArrayList}
import collection.JavaConversions._
import com.mle.wicket.component.EnabledToggle

/**
 * @author mle
 */
class UserEditPanel(id: String, editModel: IModel[EditableUser], updating: IModel[Boolean])
  extends UpdateAwareEditPanel(id, editModel, updating) with Log {
  def userManager = DatabaseSettings.userManager

  val form = new Form("editForm")
  add(form)
  // http://technically.us/code/x/the-escape-hatch/
  val usernameField = new TextField("username", RWModel[String](item.username, item.username = _)) with EnabledToggle {
    def enabled = !updating.getObject
  }
  val passwordField = new PasswordTextField("password",
    RWModel(item.password.getOrElse(""), pass => item.password = Some(pass))
  ).setRequired(false)
  val groupsModel: IModel[JList[String]] = LDModel(userManager.groups)
  val assignedGroups: IModel[JArrayList[String]] = RWModel(new JArrayList(item.groups), newGroups => item.groups = newGroups)
  val groups = new ListMultipleChoice[String]("groups", assignedGroups, groupsModel)
  form add(usernameField, passwordField, groups, submitButton, headerLabel)

  def onUpdate(updatedUser: EditableUser) {
    val username = updatedUser.username
    userManager setPassword(username, updatedUser.password.getOrElse(""))
    // Revoke/assign group membership
    // TODO move to userManager.setGroups(groups:Seq[Group])
    log info "New groups: " + updatedUser.groups
    val oldGroups = userManager.groups(username)
    val newGroups = updatedUser.groups
    val removeGroups = oldGroups.filterNot(newGroups.contains)
    val addGroups = newGroups.filterNot(oldGroups.contains)
    removeGroups foreach (g => userManager revoke(username, g))
    addGroups foreach (g => userManager assign(username, g))
    info("Updated user: " + username)
  }

  def onCreate(newUser: EditableUser) {
    val username = newUser.username
    userManager.addUser(username, newUser.password.getOrElse(""))
    newUser.groups.foreach(group => userManager.assign(username, group))
    info("Created user: " + username)
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
