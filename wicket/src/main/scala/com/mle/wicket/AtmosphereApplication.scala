package com.mle.wicket

import com.mle.util.Implicits._
import com.mle.util.Log
import com.mle.util.Scheduling._
import markup.{Single, Home}
import org.apache.wicket.Application
import org.apache.wicket.atmosphere.EventBus
import org.apache.wicket.protocol.http.WebApplication
import java.util.Date

/**
 * @author Mle
 */

class AtmosphereApplication extends WebApplication with PageMounting with Log {
  private var eBus: EventBus = null

  val getHomePage = classOf[Home]

  def eventBus = eBus

  override def init() {
    super.init()
    eBus = new EventBus(this)
    var i = 0
    every(3.seconds) {
      i += 1
      eBus post new Date
      log debug "Sent to eventbus"
    }
    mount(classOf[Home])
    mount(classOf[Single])
  }
}

object AtmosphereApplication {
  def get = Application.get.asInstanceOf[AtmosphereApplication]
}