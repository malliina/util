package com.mle.wicket

import com.mle.util.Log
import java.util.EnumSet
import javax.servlet.http.HttpServlet
import javax.servlet.{DispatcherType, Filter}
import org.apache.wicket.protocol.http._
import org.atmosphere.cache.HeaderBroadcasterCache
import org.atmosphere.cpr.AtmosphereServlet
import org.atmosphere.handler.ReflectorServletProcessor
import org.atmosphere.websocket.protocol.EchoProtocol
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.nio.SelectChannelConnector
import org.eclipse.jetty.servlet._

/**
 * https://cwiki.apache.org/WICKET/wicket-without-webxml-embedded-jetty.html
 * @author Mle
 */

object JettyUtil extends Log {
  val ATMO_PATH = "/*"
  val WS_PATH = "/ws/*"

  /**
   * Valid filters are e.g. [[org.apache.wicket.protocol.http.Jetty7WebSocketFilter]]
   * or [[org.apache.wicket.protocol.http.WicketFilter]]
   */
  def start[S <: WebApplication, T <: Filter](port: Int = 8080,
                                              wicketApp: Class[S],
                                              wicketFilter: Class[T] = classOf[WicketFilter],
                                              path: String = "/*") = {
    startServer(port)(contextHandler => {
      val filter = newWicketFilter(wicketApp, wicketFilter, path)
      contextHandler.addFilter(filter, path, EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR))
      contextHandler.addServlet(classOf[DefaultServlet], path)
    })
  }

  def startAll(port: Int = 8080) = {
    startServer(port)(c => {
      initAtmosphere(context = c)
      initWebSockets(WS_PATH, c)
    })
  }

  def startWebSockets(port: Int = 8080) = {
    startServer(port)(initWebSockets(WS_PATH, _))
  }

  def startAtmosphere[T <: WebApplication](port: Int = 8080, webApp: Class[T] = classOf[MyAtmosphereApplication]) = {
    startServer(port)(initAtmosphere(webApp, _))
  }

  private def initWebSockets(path: String, context: ServletContextHandler) {
    log info "Mapping native Web Sockets to: " + path
    val filter = newWicketFilter(classOf[TestWebApplication], classOf[Jetty7WebSocketFilter], path)
    context.addFilter(filter, path, EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR))
    context.addServlet(classOf[DefaultServlet], path)
  }

  private def initAtmosphere[T <: WebApplication](webApp: Class[T] = classOf[MyAtmosphereApplication], context: ServletContextHandler) {
    log info "Mapping atmosphere to: " + ATMO_PATH
    val servlet = new AtmosphereServlet(true)
    /**
     * Codified atmosphere.xml.
     * AtmosphereServlet automatically looks for atmosphere.xml or annotated handlers in WEB-INF/classes
     * therefore it only works for .war packages.
     * This workaround sets the AtmosphereHandler programmatically, without annotations and without atmosphere.xml
     */
    val handler = new ReflectorServletProcessor
    handler setFilterClassName classOf[WicketFilter].getName
    handler setServletClassName classOf[AtmosphereServlet].getName
    servlet.framework().addAtmosphereHandler(ATMO_PATH, handler)
    val holder = new ServletHolder(servlet)
    initWicket(holder, webApp, ATMO_PATH)
    holder setInitParameter("org.atmosphere.useWebSocket", "false")
    holder setInitParameter("org.atmosphere.useNative", "true")
    // "No AtmosphereHandler found..." unless we set EchoProtocol. hmm?
    holder setInitParameter("org.atmosphere.websocket.WebSocketProtocol", classOf[EchoProtocol].getName)
    holder setInitParameter("org.atmosphere.cpr.AtmosphereInterceptor.disableDefaults", "true")
    holder setInitParameter("org.atmosphere.cpr.broadcasterCacheClass", classOf[HeaderBroadcasterCache].getName)
    holder setInitOrder 1
    context addServlet(holder, ATMO_PATH)
  }

  def startServer(port: Int = 8080)(contextInit: ServletContextHandler => Unit): Server = {
    val server = new Server
    val connector = new SelectChannelConnector
    connector setPort port
    server addConnector connector
    val contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS)
    contextInit(contextHandler)
    server setHandler contextHandler
    server.start()
    server
  }

  /**
   * Alternative to newWicketFilter.
   * This method creates a required servlet and configures WicketFilter, but doesn't let the user choose the WicketFilter class.
   */
  def newWicketServlet[T <: WebApplication, S <: HttpServlet](webApp: Class[T],
                                                              servlet: Class[S] = classOf[WicketServlet],
                                                              path: String = "/*") = {
    initWicket(new ServletHolder(classOf[WicketServlet]), webApp)
  }

  def newWicketFilter[T <: WebApplication, U <: Filter](webApp: Class[T],
                                                        filter: Class[U] = classOf[WicketFilter],
                                                        path: String = "/*") = {
    initWicket(new FilterHolder(filter), webApp, path)
  }

  private def initWicket[T <: Holder[_], U <: WebApplication](holder: T, app: Class[U], path: String = "/*"): T = {
    holder setInitParameter(ContextParamWebApplicationFactory.APP_CLASS_PARAM, app.getName)
    holder setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, path)
    holder
  }
}