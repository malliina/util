package com.mle.wicket

import de.agilecoders.wicket.settings.{DefaultThemeProvider, BootstrapSettings}
import de.agilecoders.wicket.Bootstrap
import markup.Home
import markup.Pages.{MessagePage, SettingsPage, SortPage}
import org.apache.wicket.protocol.http.WebApplication
import collection.JavaConversions._
import org.apache.wicket.Page
import org.apache.wicket.markup.html.WebPage

/**
 * @author Mle
 */
trait Bootstrapping extends WebApplication {
  var themes: Seq[String] = Nil
  private val defaultTabs = bootTabs(
    "Home" -> classOf[Home],
    "Sorting" -> classOf[SortPage],
    "Settings" -> classOf[SettingsPage],
    "Bootstrap" -> classOf[MessagePage]
  )

  def tabs: Seq[BootTab[_ <: WebPage]] = defaultTabs

  override def init() {
    super.init()
    val settings = new BootstrapSettings()
    settings minify true // use minimized version of all bootstrap references
    // default theme
    val themeProvider = settings.getThemeProvider.asInstanceOf[DefaultThemeProvider]
    themeProvider.available().find(_.name() == "readable")
      .foreach(themeProvider.defaultTheme)
    Bootstrap.install(this, settings)
    themes = settings.getThemeProvider.available().map(_.name())
  }

  def bootTabs(tabs: (String, Class[_ <: WebPage])*) = tabs.map(pair => {
    val (title, clazz) = pair
    BootTab(title, clazz)
  })
}

case class BootTab[T <: Page](title: String, pageClass: Class[T])
