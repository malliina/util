package com.mle.homepage

import com.mle.web.wicket.{PageMounting, BootstrapApplication}
import com.mle.homepage.Pages.HomePage
import org.apache.wicket.RuntimeConfigurationType

/**
 *
 * @author mle
 */
class HomepageApplication extends BootstrapApplication with PageMounting {
  def getHomePage = classOf[HomePage]

  override def getConfigurationType = RuntimeConfigurationType.DEPLOYMENT

  override def init() {
    super.init()
    mount(classOf[HomePage])
  }
}
