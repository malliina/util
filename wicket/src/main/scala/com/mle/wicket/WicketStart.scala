package com.mle.wicket

import com.mle.util._
import java.nio.file.Paths
import com.mle.rmi.{RmiClient, RmiUtil, RmiServer}
import org.eclipse.jetty.server.Server
import ch.qos.logback.classic.Level
import com.mle.util.security.ServerKeystoreSettings
import com.mle.web.JettyUtil._

/**
 * @author Mle
 */

object WicketStart extends Log {
  val jettyCerts = ServerKeystoreSettings
  var jetty: Option[Server] = None
  var rmi: Option[RmiServer] = None

  def main(args: Array[String]) {
    if (args.size > 0 && args(0) == "stop")
      RmiClient.launchClient()
    else {
      init()
      rmi = Some(new RmiServer(keySettings = RmiUtil.keySettings) {
        override def onClosed() {
          WicketStart.this.close()
        }
      })
      AppUtils setLogLevel Level.INFO
      jetty = Some(startWebApps(8889))
    }

  }

  def init() {
    sys.props.get("wicket.home").foreach(home =>
      FileUtilities.basePath = Paths get home
    )
    RmiUtil.initSecurityPolicy()
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