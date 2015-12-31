package tests

import org.scalatest.FunSuite

/**
 *
 * @author mle
 */
class StorageTests extends FunSuite {
  test("storage units print correctly") {
    import com.malliina.storage._
    val megs = 1024 * 5
    assert(5.gigs.toString === s"$megs megabytes")
    assert(124.megs.toString === "124 megabytes")
    val giga = (1024 * 1024 * 1024).bytes
    assert(giga.toString === "1024 megabytes")
    assert(10001.kilos.toString === "10001 kilobytes")
  }
}
