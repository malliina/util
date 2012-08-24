import sbt._

/**
 * @author Mle
 */

object Dependencies {
  val slf4j = "org.slf4j" % "slf4j-api" % "1.6.6"
  val logBackClassic = "ch.qos.logback" % "logback-classic" % "1.0.6"
  val logBackCore = "ch.qos.logback" % "logback-core" % "1.0.6"
  val loggingDeps = Seq(slf4j, logBackClassic, logBackCore)
}