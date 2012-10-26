package com.mle.wicket

import com.mle.util.{FileUtilities, AppUtils, Scheduling, Log}
import com.mle.wicket.JettyUtil._
import java.nio.file.Paths
import com.mle.rmi.{RmiUtil, RmiServer}
import org.eclipse.jetty.server.Server
import ch.qos.logback.classic.Level

/**
 * @author Mle
 */

object WicketStart extends Log {
  var jetty: Option[Server] = None
  var rmi: Option[RmiServer] = None

  def main(args: Array[String]) {
    init()
    rmi = Some(new RmiServer() {
      override def onClosed() {
        WicketStart.this.close()
      }
    })
    AppUtils setLogLevel Level.INFO
    jetty = Some(startWebApps(8889))
  }

  def init() {
    sys.props.get("wicket.home").foreach(home =>
      FileUtilities.basePath = Paths get home
    )
    RmiUtil.initSecurity()
  }

  //  def startWebApps(port: Int = 8080) = startServer(port, Some(ServerKeystoreSettings), clientAuth = true)(implicit c => {
  def startWebApps(port: Int = 8080) = startServer(port)(implicit c => {
    addAtmosphere(webApp = classOf[AtmosphereApplication], path = "/atmo/*")
    addWebSockets(webApp = classOf[WebSocketsApplication], path = "/ws/*")
    serveStatic("publicweb/")
  })

  def close() {
    jetty.foreach(j => {
      j.stop()
      log info "Stopped jetty"
    })
    // wrong place
    Scheduling.shutdown()
  }
}