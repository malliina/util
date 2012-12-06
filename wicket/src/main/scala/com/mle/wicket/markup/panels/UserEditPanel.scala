package com.mle.wicket.markup.panels

import org.apache.wicket.model.IModel
import org.apache.wicket.markup.html.form._
import com.mle.util.Log
import java.util.{List => JList, ArrayList => JArrayList}
import collection.JavaConversions._
import com.mle.wicket.component.EnabledToggle
import com.mle.auth.UserManager
import com.mle.wicket.markup.AbstractUsers.EditableUser
import com.mle.web.wicket.model.{LDModel, RWModel}
import scala.Some
import com.mle.wicket.markup.AbstractUsers.EditableUser

/**
 * @author mle
 */
abstract class UserEditPanel(id: String, editModel: IModel[EditableUser], updating: IModel[Boolean])
  extends UpdateAwareEditPanel(id, editModel, updating) with Log {
  def userManager: UserManager[String]

  //  def hostManager: LdapHostManager
  val form = new Form("editForm")
  add(form)
  // http://technically.us/code/x/the-escape-hatch/
  val usernameField = new RequiredTextField("username", RWModel[String](item.username, item.username = _)) with EnabledToggle {
    def enabled = !updating.getObject
  }
  val passwordField = new PasswordTextField("password",
    RWModel(item.password.getOrElse(""), pass => item.password = Some(pass))
  ).setRequired(false)
  val groupsModel: IModel[JList[String]] = LDModel(userManager.groups)
  val assignedGroups: IModel[JArrayList[String]] = RWModel(new JArrayList(item.groups),
    newGroups => item.groups = newGroups)
  val groups = new ListMultipleChoice[String]("groups", assignedGroups, groupsModel)
  form add(usernameField, passwordField, groups, submitButton, headerLabel)

  def onUpdate(updatedUser: EditableUser) {
    val username = updatedUser.username
    updatedUser.password.foreach(pass => {
      if (pass != null && pass.nonEmpty)
        userManager setPassword(username, pass)
    })
    // Revoke/assign group membership
    userManager replaceGroups(username, updatedUser.groups)
    info("Updated user: " + username)
  }

  def onCreate(newUser: EditableUser) {
    val username = newUser.username
    userManager addUser(username, newUser.password.getOrElse(""))
    userManager assign(username, newUser.groups)
    info("Created user: " + username)
  }

  // work in progress

  //  val feedbackPanel = new NotificationPanel("feedback")
  //    if (editMode == "Create")
  //      new EditUserFragment("username", "userText")
  //    else
  //      new ReadUserFragment("username", "userLabel")
  //  private class EditUserFragment(id: String, markupId: String)
  //    extends Fragment(id, markupId, UserEditPanel.this) {
  //    add(new TextField("username", editModel))
  //  }
  //
  //  private class ReadUserFragment(id: String, markupId: String)
  //    extends Fragment(id, markupId, UserEditPanel.this) {
  //    add(new Label("username", editModel))
  //  }

}
