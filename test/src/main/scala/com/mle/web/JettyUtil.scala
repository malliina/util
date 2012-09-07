package com.mle.web

import com.mle.util.Log
import java.util.EnumSet
import javax.servlet.DispatcherType
import javax.servlet.Filter
import org.apache.wicket.protocol.http._
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{DefaultServlet, FilterHolder, ServletHolder, ServletContextHandler}

/**
 * https://cwiki.apache.org/WICKET/wicket-without-webxml-embedded-jetty.html
 * @author Mle
 */

object JettyUtil extends Log {
  def start(port: Int = 8080) {
    val server = new Server(port)
    val contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS)
    //    contextHandler addServlet(newWicketServlet(classOf[TestWebApplication]), "/*")
    // Either the following three lines, or the above one line.
    val filter = newWicketFilter(classOf[TestWebApplication], classOf[Jetty7WebSocketFilter])
    contextHandler.addFilter(filter, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR))
    contextHandler.addServlet(classOf[DefaultServlet], "/*")
    server setHandler contextHandler
    server.start()
    log info "Started web server on port: " + port
  }

  /**
   * Alternative shorthand to newWicketFilter.
   * This method creates a required servlet and configures WicketFilter, but doesn't let the user choose the WicketFilter class.
   */
  def newWicketServlet[T <: WebApplication](webApp: Class[T]) = {
    val holder = new ServletHolder(classOf[WicketServlet])
    holder setInitParameter(ContextParamWebApplicationFactory.APP_CLASS_PARAM, webApp.getName)
    holder setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*")
    holder
  }

  def newWicketFilter[T <: WebApplication, U <: Filter](webApp: Class[T],
                                                        filter: Class[U] = classOf[WicketFilter]) = {
    val fh = new FilterHolder(filter)
    fh setInitParameter(ContextParamWebApplicationFactory.APP_CLASS_PARAM, webApp.getName)
    fh setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*")
    fh
  }
}