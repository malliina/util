package com.mle.wicket

import markup.Pages.AtmospherePage


/**
 * @author Mle
 */

class AtmosphereApplication extends BasicWebApplication with Atmosphering {
  override val tabs = super.tabs :+ BootTab("Atmosphere", classOf[AtmospherePage])

  override def init() {
    super.init()
    mount(classOf[AtmospherePage])
  }
}
