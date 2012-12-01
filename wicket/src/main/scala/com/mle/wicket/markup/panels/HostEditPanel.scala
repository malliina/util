package com.mle.wicket.markup.panels

import org.apache.wicket.model.IModel
import org.apache.wicket.markup.html.form.{RequiredTextField, Form}
import com.mle.wicket.component.EnabledToggle
import com.mle.wicket.markup.Hosts.Host
import com.mle.ldap.LdapSettings.LdapHostManager
import com.mle.wicket.model.RWModel

/**
 *
 * @author mle
 */
abstract class HostEditPanel(id: String, model: IModel[Host], updating: IModel[Boolean])
  extends UpdateAwareEditPanel(id, model, updating) {
  def userManager: LdapHostManager

  val form = new Form("editForm")
  add(form)

  val hostnameField = new RequiredTextField("hostname",
    RWModel[String](item.hostname, item.hostname = _)) with EnabledToggle {
    def enabled = !updating.getObject
  }
  val ipField = new RequiredTextField("ip", RWModel[String](item.ip, item.ip = _))
  form add(hostnameField, ipField, submitButton, headerLabel)

  def onCreate(newItem: Host) {
    userManager.addHost(newItem.hostname, newItem.ip)
    info("Added group: " + newItem)
  }

  def onUpdate(updatedItem: Host) {
    info("Update not implemented")
  }
}
