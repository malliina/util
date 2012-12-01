package com.mle.wicket.markup.panels

import org.apache.wicket.model.IModel
import java.util.{ArrayList => JArrayList, List => JList}
import com.mle.wicket.model.{LDModel, RWModel}
import collection.JavaConversions._
import com.mle.ldap.LdapSettings
import org.apache.wicket.markup.html.form.ListMultipleChoice
import com.mle.util.Log
import com.mle.wicket.markup.AbstractUsers.EditableUser

/**
 *
 * @author mle
 */
trait LdapHostsEditing extends UserEditPanel with Log {
  def hostManager = LdapSettings.hostManager

  val hostsModel: IModel[JList[String]] = LDModel(hostManager.groups)
  val assignedHosts: IModel[JArrayList[String]] = RWModel(
    new JArrayList(item.hosts),
    newHosts => item.hosts = newHosts
  )
  val hostList = new ListMultipleChoice[String]("hosts", assignedHosts, hostsModel)

  form add hostList

  override def onUpdate(updatedUser: EditableUser) {
    log info "Updating"
    hostManager.replaceGroups(updatedUser.username, updatedUser.hosts)
    super.onUpdate(updatedUser)
  }

  override def onCreate(newUser: EditableUser) {
    log info "Creating"
    super.onCreate(newUser)
    hostManager assign(newUser.id, newUser.hosts)
  }
}
