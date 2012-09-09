package com.mle

import ch.qos.logback.classic.Level
import util.{AppUtils, Log}
import wicket.{MyAtmosphereApplication, JettyUtil}

/**
 * @author Mle
 */

object Test extends Log {
  def main(args: Array[String]) {
    AppUtils setLogLevel Level.INFO
    JettyUtil.startAtmosphere(webApp = classOf[MyAtmosphereApplication])
    //    JettyUtil.startWebSockets()
    //    JettyUtil.start(wicketApp = classOf[TestWebApplication])
  }
}