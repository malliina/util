package com.mle.playutil.controllers

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc._
import concurrent.Future
import play.api.libs.json.JsValue
import play.api.templates.Html
import play.api.http.MimeTypes
import com.mle.playutil.json.JsonResponse

trait BaseController extends Controller {
  def AsyncFuture(result: => Result) =
    Async(Future(result))

  def respond(html: => Result, json: => JsValue)(implicit request: RequestHeader): Result = {
    val maybeForceJson = request.getQueryString("f").map(_ == "json")
    if (maybeForceJson.isDefined) {
      NoCacheOk(json)
    } else if (request.accepts(MimeTypes.HTML)) {
      html
    } else if (request.accepts(MimeTypes.JSON)) {
      NoCacheOk(json)
    } else {
      BadRequest
    }
  }

  def response(html: => Html, json: => JsValue)(implicit request: RequestHeader): Result = {
    respond(Ok(html), json)
  }

  def okResponse(html: => Html)(implicit request: RequestHeader): Result = {
    respond(Ok(html), JsonResponse.Ok)
  }

  def NoCacheOk[C](content: C)(implicit writeable: play.api.http.Writeable[C]) = Ok(content).withHeaders(CACHE_CONTROL -> "no-cache")
}
