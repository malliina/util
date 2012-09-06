package com.mle.web

import markup.Home
import org.apache.wicket.protocol.http.WebApplication
import org.apache.wicket.markup.html.WebPage

/**
 * @author Mle
 */
class TestApplication extends WebApplication {
  def getHomePage = classOf[Home]

  override def init() {
    super.init()
    mount(classOf[Home])
  }

  /**
   * Mounts the page to the path given by the class name
   */
  def mount[T <: WebPage](pageClazz: Class[T]) {
    mountPage("/" + pageClazz.getSimpleName, pageClazz)
  }
}