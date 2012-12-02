package com.mle.web.wicket

import de.agilecoders.wicket.settings.BootstrapSettings
import de.agilecoders.wicket.Bootstrap
import org.apache.wicket.protocol.http.WebApplication

/**
 *
 * @author mle
 */
trait BootstrapApplication extends WebApplication {
  override def init() {
    super.init()
    val settings = new BootstrapSettings()
    //    log info "Using responsive CSS: " + settings.useResponsiveCss()
    settings minify true // use minimized version of all bootstrap references
    Bootstrap.install(this, settings)
  }
}
