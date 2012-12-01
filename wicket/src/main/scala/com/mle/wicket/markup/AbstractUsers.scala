package com.mle.wicket.markup

import panels.{SelectPanel, ManagementPanel}
import org.apache.wicket.model.IModel
import com.mle.wicket.model.RWModel
import com.mle.wicket.backend.WicketUserManager
import java.util.{List => JList}
import com.mle.wicket.WicketImplicits._
import com.mle.wicket.EditableItem
import com.mle.wicket.markup.AbstractUsers.{User, EditableUser}


/**
 *
 * @author mle
 */
abstract class AbstractUsers(id: String) extends ManagementPanel(id) {
  def userManager: WicketUserManager

  @transient
  var user: EditableUser = newEmptyItem
  val selectedUser: IModel[EditableUser] = RWModel(user, newUser => user = newUser)

  def newEmptyItem = EditableUser()

  class UserSelectPanel(itemId: String, selectedUser: IModel[EditableUser], usersModel: IModel[JList[EditableUser]])
    extends SelectPanel(itemId, selectedUser, usersModel, header = "Users") {
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

  def makeEditable(user: User) = EditableUser(user.userId, user.groups, user.hosts, user.password)
}

object AbstractUsers {

  case class User(userId: String = "",
                  groups: Seq[String] = Seq.empty,
                  hosts: Seq[String] = Seq.empty,
                  password: Option[String] = None)

  case class EditableUser(var username: String = "",
                          var groups: Seq[String] = Seq.empty,
                          var hosts: Seq[String] = Seq.empty,
                          var password: Option[String] = None) extends EditableItem[String] {
    def id = username
  }

}
