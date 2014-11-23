package tests

import org.scalatest.FunSuite

/**
 * I want to demonstrate contravariance. Contravariance is best understood by looking at functions.
 *
 * Contravariance: "A => C" is a "B => C" if B is a A
 *
 * @author Michael
 */
class Variance extends FunSuite {

  sealed trait Animal

  sealed trait Dog extends Animal {
    def name: String
  }

  case class Chihuahua(name: String, huahuaScore: Int) extends Dog

  case class Labrador(name: String) extends Dog

  case class Consumer[T](consume: T => Unit)

  // contravariance

  test("contravariance") {
    // so Animal => Unit is a Dog => Unit since Dog is an Animal
    Consumer[Dog]((a: Animal) => ())
    // Chihuahua => Unit is not a Dog => Unit, since it can't handle other dogs
    //    val doesNotCompile = Consumer[Dog]((a: Chihuahua) => ())

    // Array is mutable, so it's invariant
    //    val ints = Array(1, 2, 3)
    //    val anys: Array[Any] = ints

    // Seq is immutable, so it can be covariant
    val intSeq = Seq(1, 2, 3)
    val anySeq: Seq[Any] = intSeq
  }
}
