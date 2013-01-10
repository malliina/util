package com.mle.util

//import com.codahale.jerkson.Json._

/**
 * @author Mle
 */
@deprecated
object JsonUtils extends Log {
  /**
   * Converts the parameters to a valid JSON string
   * @param msg the message
   * @param version a totally arbitrary number just to test json
   * @return the message in JSON format
   */
//  def toJson(msg: String, version: java.lang.Integer = new Integer(4)): String = toJson("message" -> msg, "version" -> version)

//  def toJson(map: (String, Any)*): String = generate(Map(map: _*))

//  def parseJson(jsonData: String) = parse[Map[String, Any]](jsonData)

  case class Person(name: String, age: Int, homes: Seq[String])

  def main(args: Array[String]) {
//    val json = toJson("name" -> "Mle", "age" -> 28, "homes" -> List("Austria", "Helsinki", "Nigeria"))
//    val map = parseJson(json)
//    val person = parse[Person](json)
//    log info json
//    log info "" + map
//    log info "" + person
  }
}
