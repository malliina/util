package com.mle.wicket

import markup.Pages.{AtmospherePage, WebSocketsPage}
import org.apache.wicket.protocol.http.WebApplication
import org.apache.wicket.atmosphere.EventBus
import com.mle.util.Log


/**
 * @author Mle
 */
trait Atmosphering extends WebApplication with Log {
  private var eBus: EventBus = null

  def eventBus = eBus

  override def init() {
    super.init()
    eBus = new EventBus(this)
    var i = 0
    //    every(3 seconds) {
    //      i += 1
    //      eBus post new Date
    //      log debug "Sent to eventbus"
    //    }
  }
}
