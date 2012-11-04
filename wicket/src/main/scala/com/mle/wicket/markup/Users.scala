package com.mle.wicket.markup

import com.mle.util.Log
import com.mle.wicket.model.LDModel
import com.mle.db.DatabaseSettings
import org.apache.wicket.model.{IModel, Model}
import collection.JavaConversions._
import java.util.{List => JList}
import panels.{UserEditPanel, SelectPanel, ManagementPanel}

/**
 * @author Mle
 */
class Users(id: String) extends ManagementPanel(id) with Log {
  def userManager = DatabaseSettings.userManager

  val usersModel: IModel[JList[String]] = LDModel(userManager.users)
  val selectedUser = Model.of("")
  val selectPanel = (id: String) => new SelectPanel(id, selectedUser, usersModel) {
    def onDelete() {
      userManager.removeUser(selectedUser.getObject)
    }

    def onCreateNewSelected() {
      selectedUser.setObject("")
    }
  }
  val editPanel = (id: String) => new UserEditPanel(id, selectedUser)
  // TODO: Move to superclass without initialization NPEs
  add(sPanel, ePanel)
}