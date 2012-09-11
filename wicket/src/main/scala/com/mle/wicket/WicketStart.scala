package com.mle.wicket

import ch.qos.logback.classic.Level
import com.mle.util.{AppUtils, Log}
import com.mle.wicket.JettyUtil._

/**
 * @author Mle
 */

object WicketStart extends Log {
  val appHome = sys.props.getOrElseUpdate("wicket.home", "/opt/wicket")

  def main(args: Array[String]) {
    AppUtils setLogLevel Level.INFO
    startWebApps(8080)
  }

  def startWebApps(port: Int = 8080) = {
    startServer(port)(implicit c => {
      addAtmosphere(webApp = classOf[AtmosphereApplication], path = "/atmo/*")
      addWebSockets(webApp = classOf[TestWebApplication], path = "/ws/*")
    })
  }
}