package com.mle.wicket.markup

import panels.{HostEditPanel, SelectPanel, ManagementPanel}
import org.apache.wicket.model.IModel
import java.util.{List => JList}
import com.mle.wicket.model.{RWModel, LDModel}
import com.mle.ldap.LdapSettings
import com.mle.wicket.markup.Hosts.Host
import collection.JavaConversions._
import com.mle.wicket.WicketImplicits._
import com.mle.wicket.EditableItem

/**
 *
 * @author mle
 */
class Hosts(id: String) extends ManagementPanel(id) {
  def userManager = LdapSettings.hostManager

  val groups: IModel[JList[Host]] = LDModel(userManager.hosts)
  @transient
  var host = newEmptyItem
  val selectedGroup: IModel[Host] = RWModel(host, newHost => host = newHost)

  val selectPanel = (id: String) => new SelectPanel(id, selectedGroup, groups, header = "Hosts") {
    def onDeleteClicked(deleteItem: Host) {
      userManager removeGroup deleteItem.hostname
      info("Removed host: " + deleteItem)
    }

    def reset() {
      selectedGroup setObject newEmptyItem
    }

    itemList setChoiceRenderer ((host: Host) => host.hostname)
  }

  val editPanel = (id: String) => new HostEditPanel(id, selectedGroup, sPanel.isSelected) {
    def userManager = Hosts.this.userManager
  }
  add(sPanel, ePanel)

  def newEmptyItem = Host()
}

object Hosts {

  case class Host(var hostname: String = "", var ip: String = "") extends EditableItem[String] {
    def id = hostname
  }

}
