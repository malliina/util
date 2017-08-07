package com.malliina.http

import java.io.Closeable
import java.nio.charset.Charset
import java.util.Base64

import com.malliina.http.AsyncHttp.PromisingHandler
import org.apache.http.client.methods.{HttpGet, HttpUriRequest, RequestBuilder}
import org.apache.http.concurrent.FutureCallback
import org.apache.http.entity.{ContentType, StringEntity}
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.util.EntityUtils
import org.apache.http.{HttpEntity, HttpResponse}
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try

object AsyncHttp {
  val Utf8 = "UTF-8"
  val DefaultCharset = Try(Charset.forName(Utf8)) getOrElse Charset.defaultCharset()
  val Authorization = "Authorization"
  val Basic = "Basic"
  val ContentType = "Content-Type"
  val MimeTypeJson = "application/json"
  val WwwFormUrlEncoded = "application/x-www-form-urlencoded"

  def get(url: String)(implicit ec: ExecutionContext): Future[HttpResponse] =
    withClient(_.get(url))

  def postJson(url: String, body: JsValue, headers: Map[String, String] = Map.empty)(implicit ec: ExecutionContext): Future[HttpResponse] =
    withClient(_.post(url, body, headers))

  def post(url: String, body: String, contentType: ContentType, headers: Map[String, String] = Map.empty)(implicit ec: ExecutionContext): Future[HttpResponse] =
    withClient(_.postAny(url, body, contentType, headers))

  def withClient(run: AsyncHttp => Future[HttpResponse])(implicit ec: ExecutionContext) = {
    val client = new AsyncHttp
    val response = run(client)
    response.onComplete(_ => client.close())
    response
  }

  private class PromisingHandler(url: String) extends FutureCallback[HttpResponse] {
    val promise = Promise[HttpResponse]()

    override def failed(ex: Exception): Unit =
      promise.failure(ex)

    override def completed(result: HttpResponse): Unit =
      promise.success(result)

    override def cancelled(): Unit =
      promise.failure(new Exception(s"HTTP request to '$url' cancelled."))
  }

  implicit class RichRequestBuilder(req: HttpUriRequest) {
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

  def get(url: String): Future[HttpResponse] =
    execute(new HttpGet(url))

  def post(url: String, body: JsValue, headers: Map[String, String] = Map.empty): Future[HttpResponse] = {
    val entity = new StringEntity(Json.stringify(body), ContentType.APPLICATION_JSON)
    postEntity(url, entity, headers)
  }

  def postAny(url: String, body: String, contentType: ContentType, headers: Map[String, String] = Map.empty): Future[HttpResponse] = {
    val entity = new StringEntity(body, contentType)
    postEntity(url, entity, headers)
  }

  def postEntity(url: String,
                 entity: HttpEntity,
                 headers: Map[String, String] = Map.empty,
                 params: Map[String, String] = Map.empty): Future[HttpResponse] = {
    val builder = RequestBuilder.post(url)
    builder setEntity entity
    headers foreach { case (key, value) => builder.addHeader(key, value) }
    params foreach { case (key, value) => builder.addParameter(key, value) }
    execute(builder.build())
  }

  def execute(req: HttpUriRequest): Future[HttpResponse] =
    startExecute(req).map { r =>
      Option(r.getEntity).foreach(EntityUtils.consume)
      r
    }

  def startExecute(req: HttpUriRequest): Future[HttpResponse] = {
    val handler = new PromisingHandler(req.getURI.toString)
    client.execute(req, handler)
    handler.promise.future
  }

  def close(): Unit = client.close()
}
