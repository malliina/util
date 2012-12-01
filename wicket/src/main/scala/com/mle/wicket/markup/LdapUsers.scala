package com.mle.wicket.markup

import panels.LdapUnixUserEditPanel
import com.mle.ldap.LdapSettings
import com.mle.wicket.backend.WicketUserManager
import org.apache.wicket.model.IModel
import com.mle.wicket.model.LDModel
import java.util.{List => JList}
import com.mle.wicket.markup.AbstractUsers.EditableUser
import collection.JavaConversions._


/**
 *
 * @author mle
 */
class LdapUsers(id: String) extends AbstractUsers(id) {
  def userManager: WicketUserManager = LdapSettings.ldapUserManager

  val usersModel: IModel[JList[EditableUser]] = LDModel(userManager.userDatabase.sortBy(_.userId).map(makeEditable))

  def selectPanel = (id: String) => new LdapUserSelectPanel(id)

  def editPanel = (id: String) => new LdapUnixUserEditPanel(id, selectedUser, sPanel.isSelected) {
    def userManager = LdapUsers.this.userManager
  }

  // TODO: Move to superclass without initialization NPEs
  add(sPanel, ePanel)

  trait LdapUserSelection extends UserSelectPanel {
    override def onDeleteClicked(deleteItem: EditableUser) {
      LdapSettings.hostManager.replaceGroups(deleteItem.username, Seq.empty[String])
      super.onDeleteClicked(deleteItem)
    }
  }

  class LdapUserSelectPanel(id: String) extends UserSelectPanel(id, selectedUser, usersModel) with LdapUserSelection

}