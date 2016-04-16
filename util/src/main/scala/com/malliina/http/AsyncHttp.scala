package com.malliina.http

import java.io.Closeable
import java.nio.charset.Charset

import com.malliina.http.AsyncHttp._
import org.asynchttpclient._
import org.asynchttpclient.util.Base64
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

/**
  * A Scala [[Future]]s-based HTTP client. Wraps ning's async http client.
  *
  * Usage:
  * ```
  * import com.malliina.http.AsyncHttp._
  * val response: Future[Response] = AsyncHttp.get("http://www.google.com")
  * ```
  */
object AsyncHttp {
  val Utf8 = "UTF-8"
  val DefaultCharset = Try(Charset.forName(Utf8)) getOrElse Charset.defaultCharset()
  val Authorization = "Authorization"
  val Basic = "Basic"
  val ContentType = "Content-Type"
  val MimeTypeJson = "application/json"
  val WwwFormUrlEncoded = "application/x-www-form-urlencoded"

  def get(url: String)(implicit ec: ExecutionContext): Future[Response] = withClient(_.get(url))

  def postJson(url: String, body: JsValue, headers: Map[String, String] = Map.empty)(implicit ec: ExecutionContext): Future[Response] =
    execute(_.post(url, body), headers)

  def post(url: String, body: String, headers: Map[String, String] = Map.empty)(implicit ec: ExecutionContext): Future[Response] =
    execute(_.post(url, body), headers)

  def execute(f: AsyncHttp => BoundRequestBuilder, headers: Map[String, String])(implicit ec: ExecutionContext): Future[Response] =
    withClient(c => {
      val builder = f(c)
      headers.foreach(p => builder.setHeader(p._1, p._2))
      builder
    })

  private def withClient(f: AsyncHttp => BoundRequestBuilder)(implicit ec: ExecutionContext): Future[Response] = {
    val client = new AsyncHttp
    val response = f(client).run()
    response.onComplete(_ => client.close())
    response
  }

  implicit class RichRequestBuilder(builder: BoundRequestBuilder) {
    def addQueryParameters(parameters: (String, String)*): BoundRequestBuilder = {
      parameters.foreach(pair => builder.addQueryParam(pair._1, pair._2))
      builder
    }

    def addFormParameters(parameters: (String, String)*): BoundRequestBuilder =
      addParameters(parameters: _*)

    def addParameters(parameters: (String, String)*): BoundRequestBuilder = {
      parameters.foreach(pair => builder.addFormParam(pair._1, pair._2))
      builder
    }

    def basicAuthHeaderValue(username: String, password: String) = {
      val encodedCredentials = Base64.encode(s"$username:$password".getBytes)
      s"$Basic $encodedCredentials"
    }

    def setBasicAuth(username: String, password: String): BoundRequestBuilder = {
      builder.setHeader(Authorization, basicAuthHeaderValue(username, password))
    }

    def run(): Future[Response] = {
      val handler = new PromisingHandler
      Try(builder execute handler) match {
        case Success(_) => handler.future
        case Failure(t) => Future.failed[Response](t)
      }
    }
  }

  private class PromisingHandler extends AsyncCompletionHandler[Response] {
    private val promise = Promise[Response]()

    override def onCompleted(response: Response): Response = {
      promise success response
      response
    }

    override def onThrowable(t: Throwable): Unit = {
      promise failure t
      super.onThrowable(t)
    }

    def future = promise.future
  }

}

class AsyncHttp extends Closeable {
  val client = new DefaultAsyncHttpClient()

  def get(url: String): BoundRequestBuilder = client.prepareGet(url)

  def post(url: String, body: JsValue): BoundRequestBuilder =
    post(url, Json stringify body).setHeader(ContentType, MimeTypeJson)

  def post(url: String, body: String, charset: Charset = AsyncHttp.DefaultCharset): BoundRequestBuilder =
    client.preparePost(url).setCharset(charset).setBody(body)

  def close() = client.close()
}
