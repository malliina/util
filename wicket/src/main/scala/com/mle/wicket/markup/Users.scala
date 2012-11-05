package com.mle.wicket.markup

import com.mle.util.Log
import com.mle.wicket.model.LDModel
import com.mle.db.DatabaseSettings
import org.apache.wicket.model.IModel
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

  val usersModel: IModel[JList[EditableUser]] = LDModel(empty +: userManager.userDatabase.sortBy(_.userId).map(makeEditable))

  def makeEditable(user: DefaultJdbcUserManager#User) = EditableUser(user.userId, user.groups, user.password)

  def empty = new EditableUser("", newUser = true)

  val selectedUser = LDModel(empty)
  val selectPanel = (id: String) => new SelectPanel(id, selectedUser, usersModel) {
    def onDelete() {
      userManager.removeUser(selectedUser.getObject.username)
    }

    def onCreateNewSelected() {
      selectedUser.setObject(empty)
    }

    itemList setChoiceRenderer ((user: EditableUser) => user.username)
  }
  val creatingModel = LDModel(selectedUser.getObject.newUser)
  val editPanel = (id: String) => new UserEditPanel(id, selectedUser)
  // TODO: Move to superclass without initialization NPEs
  add(sPanel, ePanel)
}

case class EditableUser(var username: String, var groups: Seq[String] = Seq.empty, var password: Option[String] = None, newUser: Boolean = false)
