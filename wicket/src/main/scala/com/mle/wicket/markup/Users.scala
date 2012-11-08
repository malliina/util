package com.mle.wicket.markup

import com.mle.util.Log
import com.mle.wicket.model.{RWModel, LDModel}
import org.apache.wicket.model.IModel
import java.util.{List => JList, ArrayList => JArrayList}
import panels.{UserEditPanel, SelectPanel, ManagementPanel}
import com.mle.wicket.Implicits._
import com.mle.ldap.LdapSettings
import com.mle.auth.UserManager
import collection.JavaConversions._

/**
 * @author Mle
 */
class Users(id: String) extends ManagementPanel(id) with Log {
  //DatabaseSettings.userManager
  def userManager = LdapSettings.manager

  // TODO default to natural sort given by choicerenderer function
  val usersModel: IModel[JList[EditableUser]] = LDModel(userManager.userDatabase.sortBy(_.userId).map(makeEditable))
  // wicket throws SerializableExceptions without transient
  @transient
  var user: EditableUser = newEmptyItem
  val selectedUser: IModel[EditableUser] = RWModel(user, newUser => user = newUser)
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

  val editPanel = (id: String) => new UserEditPanel(id, selectedUser, sPanel.isSelected) {
    def userManager = Users.this.userManager
  }
  // TODO: Move to superclass without initialization NPEs
  add(sPanel, ePanel)

  def newEmptyItem = EditableUser("")

  def makeEditable(user: UserManager[String]#User) = EditableUser(user.userId, user.groups, user.password)
}

case class EditableUser(var username: String,
                        var groups: Seq[String] = Seq.empty,
                        var password: Option[String] = None) {
  /**
   * Compares two users.
   *
   * Usernames are unique and immutable (right?) so a username comparison is enough to compare users.
   *
   * Wicket *Choice components compare the selected item with the chioces using equals to determine if anything is "selected"
   *
   * Theoretically this method is probably not valid. I care this much: |-|
   *
   * @param obj compareTo
   * @return true if equals, false otherwise
   */
  override def equals(obj: Any) = obj match {
    case user: EditableUser => user.username == username
    case anythingElse => false
  }
}