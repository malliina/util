package com.mle.util

import org.slf4j.LoggerFactory

/**
 * @author Mle
 */

trait Log {
  protected val log = LoggerFactory.getLogger(getClass)
}