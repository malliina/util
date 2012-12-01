package com.mle.wicket.markup

import com.mle.util.Log
import com.mle.wicket.model.LDModel
import org.apache.wicket.model.IModel
import java.util.{List => JList}
import panels.UserEditPanel
import com.mle.wicket.backend.WicketUserManager
import com.mle.ldap.LdapSettings
import com.mle.wicket.markup.AbstractUsers.EditableUser
import collection.JavaConversions._

/**
 * @author Mle
 */
class Users(id: String) extends AbstractUsers(id) with Log {
  //DatabaseSettings.userManager
  def userManager: WicketUserManager = LdapSettings.userManager

  // TODO default to natural sort given by choicerenderer function
  val usersModel: IModel[JList[EditableUser]] = LDModel(userManager.userDatabase.sortBy(_.userId).map(makeEditable))

  // wicket throws SerializableExceptions without transient
  // well, this is difficult.
  def selectPanel = (id: String) => new UserSelectPanel(id, selectedUser, usersModel)

  def editPanel = (id: String) => new UserEditPanel(id, selectedUser, sPanel.isSelected) {
    def userManager = Users.this.userManager
  }

  // TODO: Move to superclass without initialization NPEs
  add(sPanel, ePanel)
}