package com.mle.wicket

import com.mle.util.Implicits._
import com.mle.util.Scheduling._
import java.util.Date
import markup.Home
import org.apache.wicket.Application
import org.apache.wicket.atmosphere.EventBus
import org.apache.wicket.protocol.http.WebApplication

/**
 * @author Mle
 */

class AtmosphereApplication extends WebApplication {
  private var eBus: EventBus = null

  def getHomePage = classOf[Home]

  def eventBus = eBus

  def getEventBus = eBus

  override def init() {
    super.init()
    // Throws :(
    eBus = new EventBus(this)
    every(3.seconds) {
      eBus post new Date()
    }
  }
}

object AtmosphereApplication {
  def get = Application.get.asInstanceOf[AtmosphereApplication]
}