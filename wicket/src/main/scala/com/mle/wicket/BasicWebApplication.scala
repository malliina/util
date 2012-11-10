package com.mle.wicket

import markup.Pages._
import markup.{BootstrapPanelPage, SoloPage}
import org.apache.wicket.protocol.http.WebApplication
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication

/**
 * JQWicket doesn't work with Wicket 6.0.0: NoClassDefFoundError: org/apache/wicket/markup/html/IHeaderResponse.
 * <br>
 * wicket-jquery-ui is not available for 6.0.0 asof now and has no sortable behavior
 * <br>
 * WiQuery is not ideal but the only one up to date and remotely working
 *
 * @author Mle
 */
class BasicWebApplication extends AuthenticatedWebApplication with Bootstrapping with PageMounting {

  override def init() {
    super.init()
    getMarkupSettings.setStripWicketTags(true)
    mount(classOf[LoginPage])
    mount(classOf[HomePage])
    mount(classOf[SettingsPage])
    mount(classOf[SoloPage])
    mount(classOf[MessagePage])
    mount(classOf[SortPage])
    mount(classOf[BootstrapPanelPage])
    mount(classOf[AccountPage])
    mount(classOf[UsersPage])
    mount(classOf[GroupsPage])
  }

  def getHomePage = classOf[MyHome]

  def getWebSessionClass = classOf[MySession]

  def getSignInPageClass = classOf[LoginPage]
}

object BasicWebApplication {
  def get = WebApplication.get().asInstanceOf[BasicWebApplication]
}
