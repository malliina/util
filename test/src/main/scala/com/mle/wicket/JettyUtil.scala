package com.mle.wicket

import com.mle.util.Log
import java.util.EnumSet
import javax.servlet.DispatcherType
import javax.servlet.Filter
import org.apache.wicket.protocol.http._
import org.atmosphere.cpr.AtmosphereServlet
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet._

/**
 * https://cwiki.apache.org/WICKET/wicket-without-webxml-embedded-jetty.html
 * @author Mle
 */

object JettyUtil extends Log {
  /**
   * Valid filters are e.g. [[org.apache.wicket.protocol.http.Jetty7WebSocketFilter]]
   * or [[org.apache.wicket.protocol.http.WicketFilter]]
   */
  def start[S <: WebApplication, T <: Filter](port: Int = 8080,
                                              wicketApp: Class[S],
                                              wicketFilter: Class[T] = classOf[WicketFilter]) {
    val server = new Server(port)
    val contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS)
    //    contextHandler addServlet(newWicketServlet(classOf[TestWebApplication]), "/*")
    // Either the following three lines, or the above one line.
    val filter = newWicketFilter(wicketApp, wicketFilter)
    contextHandler.addFilter(filter, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR))
    contextHandler.addServlet(classOf[DefaultServlet], "/*")
    server setHandler contextHandler
    server.start()
    log info "Started web server on port: " + port
  }

  def startAtmosphere(port: Int = 8080) {
    val server = new Server(port)
    val contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS)
    // Either the following three lines, or the above one line.
    //    val filter = newWicketFilter(classOf[AtmosphereApplication], classOf[WicketFilter])
    //    filter setInitParameter("org.atmosphere.useWebSocket", "true")
    //    filter setInitParameter("org.atmosphere.useNative", "true")
    //    contextHandler.addFilter(filter, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR))
    contextHandler.addServlet(classOf[AtmosphereServlet], "/*")
    server setHandler contextHandler
    server.start()
    log info "Started web server on port: " + port
  }

  /**
   * Alternative shorthand to newWicketFilter.
   * This method creates a required servlet and configures WicketFilter, but doesn't let the user choose the WicketFilter class.
   */
  def newWicketServlet[T <: WebApplication](webApp: Class[T]) = {
    init(new ServletHolder(classOf[WicketServlet]), webApp)
  }

  def newWicketFilter[T <: WebApplication, U <: Filter](webApp: Class[T],
                                                        filter: Class[U] = classOf[WicketFilter]) = {
    init(new FilterHolder(filter), webApp)
  }

  private def init[T <: Holder[_], U <: WebApplication](holder: T, app: Class[U]): T = {
    holder setInitParameter(ContextParamWebApplicationFactory.APP_CLASS_PARAM, app.getName)
    holder setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*")
    holder
  }
}