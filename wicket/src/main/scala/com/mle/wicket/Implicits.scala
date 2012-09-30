package com.mle.wicket

import org.apache.wicket.util.time.Duration


/**
 * @author Mle
 */
object Implicits {
  /**
   * Usage: <code>5.seconds</code>
   * @param i amount
   * @return the amount along with the given time unit
   */
  implicit def int2duration(i: Int) = new {
    def milliseconds = Duration.valueOf(i + " milliseconds")

    def seconds = Duration.valueOf(i + " seconds")

    def minutes = Duration.valueOf(i + " minutes")

    def hours = Duration.valueOf(i + " hours")

    def days = Duration.valueOf(i + " days")
  }
}
