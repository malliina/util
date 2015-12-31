package com.malliina.http

import java.io.Closeable
import java.nio.charset.Charset

import com.malliina.http.AsyncHttp._
import com.ning.http.client.{AsyncCompletionHandler, AsyncHttpClient, Response}
import com.ning.http.util.Base64
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
 *
 * @author mle
 */
object AsyncHttp {
  type RequestBuilder = AsyncHttpClient#BoundRequestBuilder
  val AUTHORIZATION = "Authorization"
  val BASIC = "Basic"
  val CONTENT_TYPE = "Content-Type"
  val JSON = "application/json"
  val WWW_FORM_URL_ENCODED = "application/x-www-form-urlencoded"

  def get(url: String)(implicit ec: ExecutionContext): Future[Response] = withClient(_.get(url))

  def postJson(url: String, body: JsValue, headers: Map[String, String] = Map.empty)(implicit ec: ExecutionContext): Future[Response] =
    execute(_.post(url, body), headers)

  def post(url: String, body: String, headers: Map[String, String] = Map.empty)(implicit ec: ExecutionContext): Future[Response] =
    execute(_.post(url, body), headers)

  def execute(f: AsyncHttp => RequestBuilder, headers: Map[String, String])(implicit ec: ExecutionContext): Future[Response] =
    withClient(c => {
      val builder = f(c)
      headers.foreach(p => builder.setHeader(p._1, p._2))
      builder
    })

  private def withClient(f: AsyncHttp => RequestBuilder)(implicit ec: ExecutionContext): Future[Response] = {
    val client = new AsyncHttp
    val response = f(client).run()
    response.onComplete(_ => client.close())
    response
  }

  implicit class RichRequestBuilder(builder: RequestBuilder) {
    def addQueryParameters(parameters: (String, String)*): RequestBuilder = {
      parameters.foreach(pair => builder.addQueryParam(pair._1, pair._2))
      builder
    }

    def addFormParameters(parameters: (String, String)*): RequestBuilder =
      addParameters(parameters: _*)

    def addParameters(parameters: (String, String)*): RequestBuilder = {
      parameters.foreach(pair => builder.addFormParam(pair._1, pair._2))
      builder
    }

    def basicAuthHeaderValue(username: String, password: String) = {
      val encodedCredentials = Base64.encode(s"$username:$password".getBytes)
      s"$BASIC $encodedCredentials"
    }

    def setBasicAuth(username: String, password: String): RequestBuilder = {
      builder.setHeader(AUTHORIZATION, basicAuthHeaderValue(username, password))
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
  val client = new AsyncHttpClient()

  def get(url: String): RequestBuilder = client.prepareGet(url)

  def post(url: String, body: JsValue): RequestBuilder =
    post(url, Json stringify body).setHeader(CONTENT_TYPE, JSON)

  def post(url: String, body: String, encoding: String = "UTF-8"): RequestBuilder =
    client.preparePost(url).setBodyEncoding(encoding).setBody(body)

  def close() = client.close()
}
