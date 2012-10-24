package com.mle.util

import java.util.concurrent.TimeUnit
import java.nio.file.Path

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

  /**
   * Aliases / to <code>Path.resolve</code> ala sbt
   * @param path this
   * @return path resolve next
   */
  implicit def path2path(path: Path) = new {
    def /(next: String) = path resolve next
  }
}