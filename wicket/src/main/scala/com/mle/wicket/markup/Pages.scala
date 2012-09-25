package com.mle.wicket.markup

/**
 * Page classes for cases where we have [[org.apache.wicket.markup.html.panel.Panel]]s but want [[org.apache.wicket.Page]]s.
 *
 * @author Mle
 */
object Pages {

  class WebSocketsPage extends BootstrapPage(new WebSockets(_))

  class AtmospherePage extends BootstrapPage(new Atmosphere(_))

  class SettingsPage extends BootstrapPage(new Settings(_))

  class SortPage extends BootstrapPage(new SortPanel(_))

  class MessagePage extends BootstrapPage(new MessagePanel(_))

}
