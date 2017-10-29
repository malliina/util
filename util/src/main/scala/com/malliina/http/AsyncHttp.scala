package com.malliina.http

import java.io.Closeable
import java.nio.charset.Charset
import java.util.Base64

import com.malliina.http.AsyncHttp.{ContentTypeHeaderName, PromisingHandler, RichRequestBuilder, WwwFormUrlEncoded}
import org.apache.http.client.methods.{HttpGet, HttpUriRequest, RequestBuilder}
import org.apache.http.concurrent.FutureCallback
import org.apache.http.entity.{ContentType, StringEntity}
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.{HttpEntity, HttpResponse}
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try

object AsyncHttp {
  val Authorization = "Authorization"
  val Basic = "Basic"
  val ContentTypeHeaderName = "Content-Type"
  val Utf8 = "UTF-8"
  val DefaultCharset = Try(Charset.forName(Utf8)) getOrElse Charset.defaultCharset()
  val MimeTypeJson = "application/json"
  val WwwFormUrlEncoded = "application/x-www-form-urlencoded"

  def get(url: FullUrl)(implicit ec: ExecutionContext): Future[WebResponse] =
    withClient(_.get(url))

  def postJson(url: FullUrl, body: JsValue, headers: Map[String, String] = Map.empty)(implicit ec: ExecutionContext): Future[WebResponse] =
    withClient(_.post(url, body, headers))

  def post(url: FullUrl, body: String, contentType: ContentType, headers: Map[String, String] = Map.empty)(implicit ec: ExecutionContext): Future[WebResponse] =
    withClient(_.postAny(url, body, contentType, headers))

  def withClient(run: AsyncHttp => Future[WebResponse])(implicit ec: ExecutionContext) = {
    val client = new AsyncHttp
    val response = run(client)
    response.onComplete(_ => client.close())
    response
  }

  private class PromisingHandler(url: FullUrl) extends FutureCallback[HttpResponse] {
    val promise = Promise[HttpResponse]()

    override def failed(ex: Exception): Unit =
      promise.failure(ex)

    override def completed(result: HttpResponse): Unit =
      promise.success(result)

    override def cancelled(): Unit =
      promise.failure(new Exception(s"HTTP request to '$url' cancelled."))
  }

  implicit class RichRequestBuilder(builder: RequestBuilder) {
    def withParameters(ps: Map[String, String]): RequestBuilder = {
      ps foreach { case (k, v) => builder.addParameter(k, v) }
      builder
    }

    def withHeaders(headers: Map[String, String]): RequestBuilder = {
      headers foreach { case (k, v) => builder.addHeader(k, v) }
      builder
    }
  }

  implicit class RichRequest(req: HttpUriRequest) {
    def setBasicAuth(username: String, password: String): Unit =
      req.setHeader(Authorization, basicAuthHeaderValue(username, password))

    def basicAuthHeaderValue(username: String, password: String) = {
      val encodedCredentials = Base64.getEncoder.encodeToString(s"$username:$password".getBytes)
      s"$Basic $encodedCredentials"
    }
  }

}

class AsyncHttp()(implicit ec: ExecutionContext) extends Closeable {
  val client = HttpAsyncClients.createDefault()
  client.start()

  def get(url: FullUrl): Future[WebResponse] =
    execute(new HttpGet(url.url))

  def post(url: FullUrl, body: JsValue, headers: Map[String, String] = Map.empty): Future[WebResponse] = {
    val entity = new StringEntity(Json.stringify(body), ContentType.APPLICATION_JSON)
    postEntity(url, entity, headers)
  }

  def postAny(url: FullUrl,
              body: String,
              contentType: ContentType,
              headers: Map[String, String] = Map.empty): Future[WebResponse] =
    postEntity(url, new StringEntity(body, contentType), headers)

  def postEntity(url: FullUrl, entity: HttpEntity, headers: Map[String, String]) = {
    val builder = postRequest(url, headers)
    builder setEntity entity
    execute(builder.build())
  }

  def postForm(url: FullUrl, params: Map[String, String]): Future[WebResponse] =
    postEmpty(url, Map(ContentTypeHeaderName -> WwwFormUrlEncoded), params)

  def postEmpty(url: FullUrl,
                headers: Map[String, String] = Map.empty,
                params: Map[String, String] = Map.empty): Future[WebResponse] = {
    val builder = postRequest(url, headers, params)
    execute(builder.build())
  }

  def postRequest(url: FullUrl,
                  headers: Map[String, String] = Map.empty,
                  params: Map[String, String] = Map.empty): RequestBuilder = {
    val builder = RequestBuilder.post(url.url)
    builder.withHeaders(headers).withParameters(params)
  }

  def execute(req: HttpUriRequest): Future[WebResponse] = {
    val uri = req.getURI.toString
    FullUrl.build(req.getURI.toString).map { url =>
      val handler = new PromisingHandler(url)
      client.execute(req, handler)
      handler.promise.future.map(WebResponse.apply)
    }.getOrElse {
      Future.failed(new Exception(s"Invalid URL: '$uri'."))
    }
  }

  def close(): Unit = client.close()
}
