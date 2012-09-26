package com.mle.wicket

import ch.qos.logback.classic.Level
import com.mle.util.{FileUtilities, AppUtils, Log}
import com.mle.wicket.JettyUtil._
import java.nio.file.Paths

/**
 * @author Mle
 */

object WicketStart extends Log {
  sys.props.get("wicket.home").foreach(home =>
    FileUtilities.basePath = Paths get home
  )

  def main(args: Array[String]) {
    AppUtils setLogLevel Level.INFO
    startWebApps(8080)
  }

  def startWebApps(port: Int = 8080) = {
    startServer(port)(implicit c => {
      addAtmosphere(webApp = classOf[AtmosphereApplication], path = "/atmo/*")
      addWebSockets(webApp = classOf[WebSocketsApplication], path = "/ws/*")
    })
  }
}