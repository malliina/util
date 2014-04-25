package tests

import org.scalatest.FunSuite

/**
 *
 * @author mle
 */
class LogTests extends FunSuite with TestBase {
  test("list log uris") {
    val client = newClient
    val cont = client.logContainer
//    cont.uris foreach println  // returns only http://pimp.blob.core.windows.net/$logs/blob/
  }
}
