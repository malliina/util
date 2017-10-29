package tests

import com.malliina.concurrent.ExecutionContexts.cached
import com.malliina.http.{AsyncHttp, FullUrl}
import org.scalatest.FunSuite

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class HttpTests extends FunSuite {
  test("make request") {
    val f = AsyncHttp.get(FullUrl.build("http://www.google.com").right.get)
    val res = Await.result(f, 10.seconds)
    assert(res.isSuccess)
    assert(res.asString.nonEmpty)
  }
}
