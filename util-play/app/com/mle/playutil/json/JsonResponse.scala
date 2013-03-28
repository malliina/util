package com.mle.playutil.json

import play.api.libs.json.Json

/**
 * @author Michael
 */
object JsonResponse {
  val Ok = Json.obj("status" -> "ok")
  val UnAuthorized = Json.obj("status" -> "failure", "reason" -> "access denied")
}
