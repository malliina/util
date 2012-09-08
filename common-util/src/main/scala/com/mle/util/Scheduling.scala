package com.mle.util

import java.util.concurrent.{Executors, TimeUnit}

/**
 * @author Mle
 */

object Scheduling {
  private val service = Executors.newSingleThreadScheduledExecutor()
  val every = (schedule(_: Int, _: TimeUnit) _).tupled

  def schedule(delay: Int, timeUnit: TimeUnit)(code: => Unit) = {
    service.scheduleWithFixedDelay(runnable(code), 1, delay, timeUnit)
  }

  def runnable(code: => Unit) = new Runnable {
    def run() {
      code
    }
  }
}