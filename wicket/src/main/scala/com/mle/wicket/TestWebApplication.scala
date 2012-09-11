package com.mle.wicket

import markup.{Single, Home}
import org.apache.wicket.protocol.http.WebApplication

/**
 * JQWicket doesn't work with Wicket 6.0.0: NoClassDefFoundError: org/apache/wicket/markup/html/IHeaderResponse.
 * <br>
 * WiQuery is not preferred.
 * <br>
 * wicket-jquery-ui is not available for 6.0.0 asof now and has no sortable behavior
 *
 * @author Mle
 */
class TestWebApplication extends WebApplication with PageMounting {
  def getHomePage = classOf[Home]

  override def init() {
    super.init()
//    mount(classOf[Home])
//    mount(classOf[Single])
  }
}