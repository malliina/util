package com.mle.wicket.markup

import panels.{GroupEditPanel, SelectPanel, ManagementPanel}
import org.apache.wicket.model.{Model, IModel}
import java.util.{List => JList}
import com.mle.wicket.model.LDModel
import com.mle.db.DatabaseSettings
import collection.JavaConversions._

/**
 *
 * @author mle
 */
class Groups(id: String) extends ManagementPanel(id) {
  def userManager = DatabaseSettings.userManager

  val groups: IModel[JList[String]] = LDModel(userManager.groups)
  val selectedGroup = Model.of(newEmptyItem)

  val selectPanel = (id: String) => new SelectPanel(id, selectedGroup, groups, header = "Groups") {
    def onDeleteClicked(deleteItem: String) {
      userManager removeGroup deleteItem
    }

    def reset() {
      selectedGroup setObject newEmptyItem
    }
  }

  val editPanel = (id: String) => new GroupEditPanel(id, selectedGroup, sPanel.isSelected)
  add(sPanel, ePanel)

  def newEmptyItem = ""
}
