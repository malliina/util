package com.mle.util.tests

import org.scalatest.FunSuite
import com.mle.util.{Reflection}

/**
 *
 * @author mle
 */
class UtilTests extends FunSuite {
//  test("JSON map is identical after serialization-deserialization") {
//    val json = JsonUtils.toJson("name" -> "Mle", "age" -> 28, "homes" -> List("Austria", "Helsinki", "Nigeria"))
//    val firstMap = JsonUtils.parseJson(json)
//    val jsonSerialized = JsonUtils.toJson(firstMap.toSeq: _*)
//    val secondMap = JsonUtils.parseJson(jsonSerialized)
//    assert(firstMap === secondMap)
//  }
  test("random reflection") {
    assert(Reflection.className(TestObj) === "TestObj")
    val names = Reflection.names(TestObj)
    val fields = Seq("a", "myVar")
    assert(fields.intersect(names) === fields)
    assert(Reflection.fieldName(TestObj, TestObj.myVar) === "myVar")
    println(Reflection.objects(TestObj).mkString(", "))
    println(TestObj.name + ", " + TestObj.ocol.name)
  }

  object TestObj {
    val a = "value of a"
    val myVar = "yoyoo"

    object ocol {
      override def toString = "This is object o"

      def name = Reflection.className(this)
    }

    def name = Reflection.className(this)

  }

}
