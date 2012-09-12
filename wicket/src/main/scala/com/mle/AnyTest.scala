package com.mle

import util.Log

/**
 * Todo:
 * deploy to heroku
 * make tabbedpanel bookmarkable
 * finish native packaging work
 *
 * @author Mle
 */

object AnyTest extends Log {
  def main(args: Array[String]) {
    log info "Hello"
    val json_format = """{"year": %s, "field": %s, "value": %s}"""
    val data: Seq[AnyRef] = Seq(new Integer(2012), new Integer(4), "hohoi")
    val res = String.format(json_format, data: _*)
    log info json_format
    log info res
  }
}