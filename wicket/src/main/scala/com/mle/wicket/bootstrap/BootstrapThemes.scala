package com.mle.wicket.bootstrap

import de.agilecoders.wicket.settings.{DefaultThemeProvider, BootstrapSettings}
import collection.JavaConversions._

/**
 * @author Mle
 */
class BootstrapThemes(settings: BootstrapSettings, defaultTheme: String = "readable") {
  private val themeProvider = settings.getThemeProvider.asInstanceOf[DefaultThemeProvider]
  val themeNames = themeProvider.available().map(_.name()).toList
  themeNames.find(_ == defaultTheme).foreach(themeProvider.defaultTheme)

  def active = settings.getActiveThemeProvider.getActiveTheme.name()

  def activate(theme: String) {
    settings.getActiveThemeProvider setActiveTheme theme
  }
}
