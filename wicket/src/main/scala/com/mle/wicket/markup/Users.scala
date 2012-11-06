package com.mle.wicket.markup

import com.mle.util.Log
import com.mle.wicket.model.LDModel
import com.mle.db.DatabaseSettings
import org.apache.wicket.model.{Model, IModel}
import collection.JavaConversions._
import java.util.{List => JList}
import panels.{UserEditPanel, SelectPanel, ManagementPanel}
import com.mle.jdbc.auth.DefaultJdbcUserManager
import com.mle.wicket.Implicits._

/**
 * @author Mle
 */
class Users(id: String) extends ManagementPanel(id) with Log {
  def userManager = DatabaseSettings.userManager

  // TODO default to natural sort given by choicerenderer function
  val usersModel: IModel[JList[EditableUser]] = LDModel(userManager.userDatabase.sortBy(_.userId).map(makeEditable))

  def makeEditable(user: DefaultJdbcUserManager#User) = EditableUser(user.userId, user.groups, user.password)

  val empty = new EditableUser("", newUser = true)

  val selectedUser = Model.of(empty)
  val selectPanel = (id: String) => new SelectPanel(id, selectedUser, usersModel) {
    def onDeleteClicked(deleteItem: EditableUser) {
      userManager.removeUser(deleteItem.username)
      selectedUser setObject empty
    }

    def onCreateNewSelected() {
      selectedUser.setObject(empty)
    }

    itemList setChoiceRenderer ((user: EditableUser) => user.username)
  }
  val editPanel = (id: String) => new UserEditPanel(id, selectedUser)
  // TODO: Move to superclass without initialization NPEs
  add(sPanel, ePanel)
}
// TODO separate newUser into some generic newItem flag stored elsewhere
case class EditableUser(var username: String,
                        var groups: Seq[String] = Seq.empty,
                        var password: Option[String] = None,
                        newUser: Boolean = false)
