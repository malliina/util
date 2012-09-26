package com.mle.util

/**
 * @author Mle
 */
object JsonUtils {
  val JSON_FORMAT = """{"message": "%s", "version": %d}"""

  /**
   * Converts the parameters to a valid JSON string
   * @param msg the message
   * @param version a totally arbitrary number just to test json
   * @return the message in JSON format
   */
  def toJson(msg: String, version: java.lang.Integer = new Integer(4)) = String.format(JSON_FORMAT, msg, version)
}
