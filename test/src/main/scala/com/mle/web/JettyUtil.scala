package com.mle.web

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletHolder, ServletContextHandler}
import org.apache.wicket.protocol.http.{WebApplication, WicketFilter, ContextParamWebApplicationFactory, WicketServlet}
import com.mle.util.Log

/**
 * @author Mle
 */

object JettyUtil extends Log {
  def start(port: Int = 8080) {
    val server = new Server(port)
    val contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS)
    contextHandler addServlet(newWicketServlet(classOf[TestWebApplication]), "/*")
    server setHandler contextHandler
    server.start()
    log info "Started web server on port: " + port
  }

  def newWicketServlet[T <: WebApplication](webApp: Class[T]) = {
    val holder = new ServletHolder(classOf[WicketServlet])
    holder setInitParameter(ContextParamWebApplicationFactory.APP_CLASS_PARAM, webApp.getName)
    holder setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*")
    holder
  }
}