package com.mle

import ch.qos.logback.classic.Level
import util.{AppUtils, Log}
import wicket.JettyUtil._
import wicket.{TestWebApplication, MyAtmosphereApplication}

/**
 * @author Mle
 */

object WicketStart extends Log {
  def main(args: Array[String]) {
    AppUtils setLogLevel Level.INFO
    startWebApps(8080)
    //    JettyUtil.startWebSockets()
    //    JettyUtil.start(wicketApp = classOf[TestWebApplication])
  }

  def startWebApps(port: Int = 8080) = {
    startServer(port)(implicit c => {
//      initAtmosphere(webApp = classOf[MyAtmosphereApplication], path = "/atmo/*")
      initWebSockets(webApp = classOf[TestWebApplication], path = "/*")
    })
  }
}