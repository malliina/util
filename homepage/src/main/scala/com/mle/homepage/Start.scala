package com.mle.homepage

import com.mle.web.JettyUtil._

/**
 *
 * @author mle
 */
object Start {
  def main(args: Array[String]) {
    startServer()(implicit context => {
      addWicket(classOf[HomepageApplication], path = "/home/*")
      serveStatic("publicweb/")
    })
  }
}
