package com.mle.util

import java.util.concurrent.TimeUnit
import java.nio.file.Path
import java.util.Properties
import collection.JavaConversions._

/**
 * @author Mle
 */

object Implicits {
  /**
   * Usage: <code>5.seconds</code>
   * @param i amount
   * @return the amount along with the given time unit
   */
  implicit def int2timeUnits(i: Int) = new {
    def milliseconds = (i, TimeUnit.MILLISECONDS)

    def seconds = (i, TimeUnit.SECONDS)

    def minutes = (i, TimeUnit.MINUTES)

    def hours = (i, TimeUnit.HOURS)

    def days = (i, TimeUnit.DAYS)
  }

  implicit def code2callable[T](code: => T) = Scheduling.callable(code)

  implicit def map2props(map: collection.Map[_ <: AnyRef, _ <: AnyRef]) = new {
    def toProperties = {
      val props = new Properties
      props putAll map
      props
    }
  }
}