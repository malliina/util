package tests

import com.malliina.concurrent.ExecutionContexts.cached
import com.malliina.http.AsyncHttp
import org.scalatest.FunSuite

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class HttpTests extends FunSuite {
  test("make request") {
    val f = AsyncHttp.get("http://www.google.com")
    val res = Await.result(f, 10.seconds)
    assert(res.isSuccess)
    assert(res.asString.nonEmpty)
  }
}
