package com.mle.wicket

import markup.Pages.SettingsPage
import markup.{BootstrapNav => BootstrapPage, SingleChild, Single, Home}
import org.apache.wicket.protocol.http.WebApplication
import de.agilecoders.wicket.settings.BootstrapSettings
import de.agilecoders.wicket.Bootstrap

/**
 * JQWicket doesn't work with Wicket 6.0.0: NoClassDefFoundError: org/apache/wicket/markup/html/IHeaderResponse.
 * <br>
 * WiQuery is not preferred.
 * <br>
 * wicket-jquery-ui is not available for 6.0.0 asof now and has no sortable behavior
 *
 * @author Mle
 */
class TestWebApplication extends WebApplication with PageMounting {
  def getHomePage = classOf[Home]

  override def init() {
    super.init()
    val settings = new BootstrapSettings()
    settings minify true // use minimized version of all bootstrap references
    Bootstrap.install(this, settings)

    mount(classOf[Home])
    mount(classOf[Single])
    mount(classOf[SingleChild])
    mount(classOf[SettingsPage])

  }
}