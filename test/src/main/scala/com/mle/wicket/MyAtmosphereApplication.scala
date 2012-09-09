package com.mle.wicket

import com.mle.util.Implicits._
import com.mle.util.Log
import com.mle.util.Scheduling._
import java.util.Date
import markup.{Single, Home}
import org.apache.wicket.Application
import org.apache.wicket.atmosphere.EventBus
import org.apache.wicket.protocol.http.WebApplication

/**
 * @author Mle
 */

class MyAtmosphereApplication extends WebApplication with PageMounting with Log {
  private var eBus: EventBus = null

  val getHomePage = classOf[Home]

  def eventBus = eBus

  override def init() {
    super.init()
    eBus = new EventBus(this)
    every(3.seconds) {
      eBus post new Date()
      log info "Posted new date"
    }
    mount(classOf[Home])
    mount(classOf[Single])
  }
}

object MyAtmosphereApplication {
  def get = Application.get.asInstanceOf[MyAtmosphereApplication]
}