package com.mle

import ch.qos.logback.classic.Level
import util.{AppUtils, Log}
import wicket.JettyUtil
import wicket.javastuff.AtmosphereApplication

/**
 * @author Mle
 */

object Test extends Log {
  def main(args: Array[String]) {
    AppUtils setLogLevel Level.INFO
    JettyUtil.startAtmosphere(webApp = classOf[AtmosphereApplication])
    //    JettyUtil.startWebSockets()
    //    JettyUtil.start(wicketApp = classOf[TestWebApplication])
  }
}