package com.mle.wicket

import markup.Pages.{MessagePage, SortPage, SettingsPage}
import markup.{BootstrapNav => BootstrapPage, Home}
import org.apache.wicket.protocol.http.WebApplication

/**
 * JQWicket doesn't work with Wicket 6.0.0: NoClassDefFoundError: org/apache/wicket/markup/html/IHeaderResponse.
 * <br>
 * wicket-jquery-ui is not available for 6.0.0 asof now and has no sortable behavior
 * <br>
 * WiQuery is not ideal but the only one up to date and remotely working
 *
 * @author Mle
 */
class BasicWebApplication extends Bootstrapping with PageMounting {
  def getHomePage = classOf[Home]

  override def init() {
    super.init()
    mount(classOf[Home])
    mount(classOf[SettingsPage])
    mount(classOf[MessagePage])
    mount(classOf[SortPage])
  }
}

object BasicWebApplication {
  def get = WebApplication.get().asInstanceOf[BasicWebApplication]
}
