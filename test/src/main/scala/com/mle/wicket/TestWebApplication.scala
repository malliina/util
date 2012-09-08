package com.mle.wicket

import markup.Home
import org.apache.wicket.protocol.http.WebApplication
import org.apache.wicket.markup.html.WebPage

/**
 * JQWicket doesn't work with Wicket 6.0.0: NoClassDefFoundError: org/apache/wicket/markup/html/IHeaderResponse.
 * <br>
 * WiQuery is not preferred.
 * <br>
 * wicket-jquery-ui is not available for 6.0.0 asof now and has no sortable behavior
 *
 * @author Mle
 */
class TestWebApplication extends WebApplication {
  val getHomePage = classOf[Home]

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