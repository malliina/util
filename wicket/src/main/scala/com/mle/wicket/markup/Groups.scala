package com.mle.wicket.markup

import panels.{GroupEditPanel, SelectPanel, ManagementPanel}
import org.apache.wicket.model.{Model, IModel}
import java.util.{List => JList}
import com.mle.wicket.model.LDModel
import collection.JavaConversions._
import com.mle.ldap.LdapSettings

/**
 *
 * @author mle
 */
class Groups(id: String) extends ManagementPanel(id) {
  def userManager = LdapSettings.manager //DatabaseSettings.userManager

  val groups: IModel[JList[String]] = LDModel(userManager.groups)
  val selectedGroup = Model.of(newEmptyItem)

  val selectPanel = (id: String) => new SelectPanel(id, selectedGroup, groups, header = "Groups") {
    def onDeleteClicked(deleteItem: String) {
      userManager removeGroup deleteItem
      info("Removed group: " + deleteItem)
    }

    def reset() {
      selectedGroup setObject newEmptyItem
    }
  }

  val editPanel = (id: String) => new GroupEditPanel(id, selectedGroup, sPanel.isSelected) {
    def userManager = Groups.this.userManager
  }
  add(sPanel, ePanel)

  def newEmptyItem = ""
}
