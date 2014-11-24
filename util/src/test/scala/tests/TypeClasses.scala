package tests

import org.scalatest.FunSuite
import tests.Math.NumberLike

import scala.concurrent.duration.{Duration, DurationLong}

/**
 * @author Michael
 * @see http://danielwestheide.com/blog/2013/02/06/the-neophytes-guide-to-scala-part-12-type-classes.html
 */
class TypeClasses extends FunSuite {
  test("mean ints") {
    Statistics.mean(Vector(1, 2, 3))
    implicit val str = new NumberLike[String] {
      override def plus(x: String, y: String): String = x ++ y

      override def divide(x: String, y: Int): String = x.take(x.size / y)

      override def minus(x: String, y: String): String = x take y.size
    }
    Statistics.mean(Vector("a", "b", "c"))

    implicit val dur = new NumberLike[Duration] {
      override def plus(x: Duration, y: Duration): Duration = (x.toNanos + y.toNanos).nanos

      override def divide(x: Duration, y: Int): Duration = (x.toNanos / y).nanos

      override def minus(x: Duration, y: Duration): Duration = (x.toNanos - y.toNanos).nanos
    }
    Statistics.mean(Vector[Duration](10.nanos, 34.nanos))
  }
}

