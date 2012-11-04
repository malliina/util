package com.mle.wicket

import model.LDModel
import org.apache.wicket.util.time.Duration
import org.apache.wicket.model.IModel


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

  implicit def model2model[T, U](model: IModel[T]) = new {
    def map(transformer: T => U): IModel[U] = LDModel(transformer(model.getObject))
  }
}
