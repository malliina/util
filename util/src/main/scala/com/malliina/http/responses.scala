package com.malliina.http

import java.nio.charset.StandardCharsets

import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils
import play.api.libs.json._

import scala.util.Try

object WebResponse {
  def apply(inner: HttpResponse): WebResponse = new WebResponse(inner)
}

class WebResponse(val inner: HttpResponse) extends ResponseLike {
  lazy val asString: String = EntityUtils.toString(inner.getEntity, StandardCharsets.UTF_8)

  def json: Try[JsValue] = Try(Json.parse(asString))

  def parse[T: Reads] = json.map(_.validate[T]).getOrElse(JsError(s"Not JSON: '$asString'."))

  def code: Int = inner.getStatusLine.getStatusCode
}

trait ResponseLike {
  /**
    * @return the body as a string
    */
  def asString: String

  def json: Try[JsValue]

  def parse[T: Reads]: JsResult[T]

  def code: Int

  def isSuccess = code >= 200 && code < 400
}
