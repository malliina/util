package com.mle.util

import java.util.concurrent.{Callable, Executors, TimeUnit}

/**
 * @author Mle
 */

object Scheduling extends Log {
  private val service = Executors.newSingleThreadScheduledExecutor()
  val every = (schedule(_: Int, _: TimeUnit) _).tupled

  def schedule(delay: Int, timeUnit: TimeUnit)(code: => Unit) = {
    service.scheduleWithFixedDelay(runnable(code), 1, delay, timeUnit)
  }

  def runnable(code: => Unit) = new Runnable {
    def run() {
      logAnyError(code)
    }
  }

  def callable[T](code: => T) = new Callable[T] {
    def call(): T = logAnyError(code)
  }

  def logAnyError[T](code: => T) = try {
    code
  } catch {
    case e: Exception =>
      log warn("Execution failed", e)
      throw e
  }
}