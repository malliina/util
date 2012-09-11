package com.mle.util

import ch.qos.logback.classic.Level
import org.slf4j.{Logger, LoggerFactory}

/**
 * @author Mle
 */

object AppUtils extends Log {
  private[this] val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME).asInstanceOf[ch.qos.logback.classic.Logger]

  def setLogLevel(newLevel: Level) {
    rootLogger setLevel newLevel
    // Obviously ...
    log info "Log level set to: " + newLevel
  }

  def getLogLevel = rootLogger.getLevel
}