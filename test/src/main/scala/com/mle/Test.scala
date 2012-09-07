package com.mle

import util.Log
import web.JettyUtil

/**
 * @author Mle
 */

object Test extends Log {
  def main(args: Array[String]) {
    JettyUtil.start()
  }
}