package com.mle.wicket.markup

/**
 * Page classes for cases where we have [[org.apache.wicket.markup.html.panel.Panel]]s but want [[org.apache.wicket.Page]]s.
 *
 * @author Mle
 */
object Pages {

  class HomePage extends BootstrapPage(new Home(_))

  class WebSocketsPage extends BootstrapPage(new WebSocketTabs(_))

  class AtmospherePage extends BootstrapPage(new Atmosphere(_))

  class SettingsPage extends BootstrapPage(new BootstrapPanel(_))

  class SortPage extends BootstrapPage(new SortPanel(_))

  class MessagePage extends BootstrapPage(new SubTabs(_))

  class AccountPage extends BootstrapPage(new AccountPanel(_))

}
