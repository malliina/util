package com.mle.web

import com.mle.util.{Util, Log}
import java.util.EnumSet
import javax.servlet.{DispatcherType, Filter}
import org.atmosphere.cache.HeaderBroadcasterCache
import org.atmosphere.cpr.AtmosphereServlet
import org.atmosphere.handler.ReflectorServletProcessor
import org.atmosphere.websocket.protocol.EchoProtocol
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.nio.SelectChannelConnector
import org.eclipse.jetty.servlet._
import collection.JavaConversions._
import com.mle.util.security.IKeystoreSettings
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.apache.wicket.protocol.http.{ContextParamWebApplicationFactory, WicketFilter, Jetty7WebSocketFilter, WebApplication}

/**
 * https://cwiki.apache.org/WICKET/wicket-without-webxml-embedded-jetty.html
 * @author Mle
 */

object JettyUtil extends Log {

  /**
   * Valid filters are e.g. [[org.apache.wicket.protocol.http.Jetty7WebSocketFilter]]
   * or [[org.apache.wicket.protocol.http.WicketFilter]]
   */
  def addWicket[T <: WebApplication, U <: Filter](webApp: Class[T],
                                                  path: String,
                                                  filter: Class[U] = classOf[WicketFilter])(implicit context: ServletContextHandler) {
    log info "Mapping Wicket app to: " + path
    val holder = addWicketParameters(new FilterHolder(filter), webApp, path)
    context addFilter(holder, path, EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR))
    context addServlet(classOf[DefaultServlet], path)
  }

  /**
   * Adds a Wicket app with WebSockets enabled.
   * @param webApp
   * @param path pathSpec
   * @tparam T app class
   */
  def addWebSockets[T <: WebApplication](webApp: Class[T], path: String)(implicit context: ServletContextHandler) {
    addWicket(webApp, path, filter = classOf[Jetty7WebSocketFilter])
  }

  def addAtmosphere[T <: WebApplication](webApp: Class[T], path: String)(implicit context: ServletContextHandler) = {
    log info "Mapping Atmosphere app to: " + path
    val atmoServlet = newAtmosphereServlet(path)
    val holder = new ServletHolder(atmoServlet)
    addWicketParameters(holder, webApp, path)
    addAtmosphereParameters(holder)
    context addServlet(holder, path)
    atmoServlet
  }

  /**
   * Serves the files located at the given resource path from the specified web path.
   *
   * @param resourceDir a directory containing static files
   * @param webPath the path spec
   * @param context context for the servlet
   * @param dirAllowed whether directory listing is allowed
   * @return the static servlet
   */
  def serveStatic(resourceDir: String, webPath: String = "/*", dirAllowed: Boolean = false)(implicit context: ServletContextHandler) = {
    val staticUrl = Util.resource(resourceDir).toExternalForm
    log info "Mapping static files in: " + staticUrl + " to: " + webPath
    val resourceServlet = new ServletHolder(classOf[DefaultServlet])
    resourceServlet.setInitParameter("dirAllowed", dirAllowed.toString)
    resourceServlet.setInitParameter("resourceBase", staticUrl)
    resourceServlet.setInitParameter("pathInfoOnly", "true")
    context.addServlet(resourceServlet, webPath)
    resourceServlet
  }

  private def addWicketParameters[T <: Holder[_], U <: WebApplication](holder: T, app: Class[U], path: String): T = {
    holder setInitParameter(ContextParamWebApplicationFactory.APP_CLASS_PARAM, app.getName)
    holder setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, path)
    holder
  }

  def addAtmosphereParameters(holder: ServletHolder) {
    holder setInitParameter("org.atmosphere.useWebSocket", "false") // if true, ie9? works but firefox, IE10 don't
    holder setInitParameter("org.atmosphere.useNative", "true")
    // "No AtmosphereHandler found..." unless we set EchoProtocol. hmm?
    holder setInitParameter("org.atmosphere.websocket.WebSocketProtocol", classOf[EchoProtocol].getName)
    holder setInitParameter("org.atmosphere.cpr.AtmosphereInterceptor.disableDefaults", "true")
    holder setInitParameter("org.atmosphere.cpr.broadcasterCacheClass", classOf[HeaderBroadcasterCache].getName)
    holder setInitOrder 1
  }

  def newAtmosphereServlet(path: String) = {
    val servlet = new AtmosphereServlet(true) {
      /**
       * Bugfix: Kills some [[java.util.concurrent.ExecutorService]] that's otherwise left open.
       */
      override def destroy() {
        super.destroy()
        framework().getAtmosphereConfig.handlers().map(_._2.broadcaster.destroy())
      }
    }
    /**
     * Codified atmosphere.xml.
     * AtmosphereServlet automatically looks for atmosphere.xml or annotated handlers in WEB-INF/classes
     * therefore it only works for .war packages.
     * This workaround sets the AtmosphereHandler programmatically, without annotations and without atmosphere.xml
     */
    val handler = new ReflectorServletProcessor
    handler setFilterClassName classOf[WicketFilter].getName
    handler setServletClassName classOf[AtmosphereServlet].getName
    servlet.framework().addAtmosphereHandler(path, handler)
    servlet
  }

  def startServer(port: Int = 8080, keystoreSettings: Option[IKeystoreSettings] = None, clientAuth: Boolean = false)(contextInit: ServletContextHandler => Unit): Server = {
    val server = new Server
    val connector = keystoreSettings.map(newSslConnector(_, clientAuth)).getOrElse(newHttpConnector)
    connector setPort port
    server addConnector connector
    val contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS)
    contextInit(contextHandler)
    server setHandler contextHandler
    val pathSpecs = contextHandler.getServletHandler.getServletMappings.flatMap(_.getPathSpecs).mkString(", ")
    server.start()
    val host = Option(connector getHost) getOrElse "0.0.0.0"
    val protocol = if (connector.isInstanceOf[SslSelectChannelConnector]) "https" else "http"
    log info "Server started on: " + protocol + "://" + host + ":" + connector.getPort + ", paths: " + pathSpecs
    server
  }

  def newHttpConnector = new SelectChannelConnector

  def newSslConnector(keys: IKeystoreSettings, clientAuth: Boolean = false) = {
    val factory = new SslContextFactory
    factory setKeyStorePath keys.keystoreUrl.toExternalForm
    factory setKeyStorePassword keys.keystorePass
    factory setTrustStore keys.truststoreUrl.toExternalForm
    factory setTrustStorePassword keys.truststorePass
    factory setNeedClientAuth clientAuth
    new SslSelectChannelConnector(factory)
  }
}