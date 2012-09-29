package com.mle.wicket

import de.agilecoders.wicket.settings.{DefaultThemeProvider, BootstrapSettings}
import de.agilecoders.wicket.Bootstrap
import markup.Pages._
import org.apache.wicket.protocol.http.WebApplication
import collection.JavaConversions._
import org.apache.wicket.Page
import org.apache.wicket.markup.html.WebPage
import com.mle.util.Log

/**
 * @author Mle
 */
trait Bootstrapping extends WebApplication with Log {
  var themes: Seq[String] = Nil
  private val defaultTabs = buildTabs(
    "Home" -> classOf[HomePage],
    "Sorting" -> classOf[SortPage],
    "Settings" -> classOf[SettingsPage],
    "Fluid Settings" -> classOf[FluidSettingsPage],
    "MOTD" -> classOf[MessagePage]
  )

  def tabs: Seq[BootTab[_ <: WebPage]] = defaultTabs

  override def init() {
    super.init()
    val settings = new BootstrapSettings()
//    settings.
//    log info "Using responsive CSS: " + settings.useResponsiveCss()
    settings minify true // use minimized version of all bootstrap references
    themes = initThemes(settings)
    Bootstrap.install(this, settings)
  }

  private def initThemes(settings: BootstrapSettings) = {
    // default theme
    val themeProvider = settings.getThemeProvider.asInstanceOf[DefaultThemeProvider]
    val themeNames = settings.getThemeProvider.available().map(_.name())
    themeNames.find(_ == "readable").foreach(themeProvider.defaultTheme)
    themeNames
  }

  def buildTabs(tabs: (String, Class[_ <: WebPage])*) = tabs.map(pair => {
    val (title, clazz) = pair
    BootTab(title, clazz)
  })
}

case class BootTab[T <: Page](title: String, pageClass: Class[T])
