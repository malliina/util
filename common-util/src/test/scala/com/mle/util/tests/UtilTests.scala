package com.mle.util.tests

import org.scalatest.FunSuite
import com.mle.util.JsonUtils

/**
 *
 * @author mle
 */
class UtilTests extends FunSuite {
  test("JSON map is identical after serialization-deserialization") {
    val json = JsonUtils.toJson("name" -> "Mle", "age" -> 28, "homes" -> List("Austria", "Helsinki", "Nigeria"))
    val firstMap = JsonUtils.parseJson(json)
    val jsonSerialized = JsonUtils.toJson(firstMap.toSeq: _*)
    val secondMap = JsonUtils.parseJson(jsonSerialized)
    assert(firstMap === secondMap)
  }
}
