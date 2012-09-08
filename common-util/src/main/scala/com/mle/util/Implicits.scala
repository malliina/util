package com.mle.util

import java.util.concurrent.TimeUnit

/**
 * @author Mle
 */

object Implicits {
  implicit def int2timeUnits(i: Int) = new {
    def milliseconds = (i, TimeUnit.MILLISECONDS)

    def seconds = (i, TimeUnit.SECONDS)

    def minutes = (i, TimeUnit.MINUTES)

    def hours = (i, TimeUnit.HOURS)

    def days = (i, TimeUnit.DAYS)
  }

  implicit def code2callable[T](code: => T) = Scheduling.callable(code)
}