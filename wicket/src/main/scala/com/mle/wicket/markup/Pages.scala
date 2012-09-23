package com.mle.wicket.markup

/**
 * Page classes for cases where [[org.apache.wicket.markup.html.panel.Panel]]s are not desirable.
 *
 * @author Mle
 */
object Pages {

  class WebSocketsPage extends PanelPage(new WebSockets(_))

  class AtmospherePage extends PanelPage(new Atmosphere(_))

  class SettingsPage extends PanelPage(new Settings(_))

  class SortPage extends PanelPage(new SortPanel(_))

  class BootstrapPage extends PanelPage(new BootstrapTest(_))

}
