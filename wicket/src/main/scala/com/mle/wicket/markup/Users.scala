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
  val selectedUser = Model.of(newEmptyItem)
  // well, this is difficult.
  val selectPanel = (id: String) => new SelectPanel(id, selectedUser, usersModel, header = "Users") {
    def onDeleteClicked(deleteItem: EditableUser) {
      val username = deleteItem.username
      userManager removeUser username
      info("Removed user: " + deleteItem.username)
      reset()
    }

    itemList setChoiceRenderer ((user: EditableUser) => user.username)

    def reset() {
      selectedUser setObject newEmptyItem
    }
  }

  val editPanel = (id: String) => new UserEditPanel(id, selectedUser, sPanel.isSelected)
  // TODO: Move to superclass without initialization NPEs
  add(sPanel, ePanel)

  def newEmptyItem = new EditableUser("")

  def makeEditable(user: DefaultJdbcUserManager#User) = EditableUser(user.userId, user.groups, user.password)
}

case class EditableUser(var username: String,
                        var groups: Seq[String] = Seq.empty,
                        var password: Option[String] = None) {
  // wicket *Choice components compare the selected item with the chioces using equals to determine if anything is "selected"
  override def equals(obj: Any) = obj.asInstanceOf[EditableUser].username == username
}

trait Identifiable[T] {
  def id: T
}
