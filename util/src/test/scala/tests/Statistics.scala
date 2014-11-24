package tests

/**
 * @author Michael
 */
object Statistics {

  import tests.Math.NumberLike

  def mean[T](xs: Vector[T])(implicit ev: NumberLike[T]): T =
    ev.divide(xs.reduce(ev.plus(_, _)), xs.size)

  // `T: NumberLike` can replace an implicit parameter with only one parameter type
  def mean2[T: NumberLike](xs: Vector[T]): T = {
    val ev = implicitly[NumberLike[T]]
    ev.divide(xs.reduce(ev.plus), xs.size)
  }

  def median[T: NumberLike](xs: Vector[T]): T = xs(xs.size / 2)

  def quartiles[T: NumberLike](xs: Vector[T]): (T, T, T) =
    (xs(xs.size / 4), median(xs), xs(xs.size / 4 * 3))

  def iqr[T: NumberLike](xs: Vector[T]): T = quartiles(xs) match {
    case (lowerQuartile, _, upperQuartile) =>
      implicitly[NumberLike[T]].minus(upperQuartile, lowerQuartile)
  }
}

