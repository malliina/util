import sbt._

/**
 * @author Mle
 */

object Dependencies {
  val logbackVersion = "1.0.6"
  // Newest maven version gives a dependency resolution error
  val jettyVersion = "8.1.0.v20120127"
  val wicketVersion = "6.0.0"
  val slf4j = "org.slf4j" % "slf4j-api" % "1.6.6"
  val logBackClassic = "ch.qos.logback" % "logback-classic" % logbackVersion
  val logBackCore = "ch.qos.logback" % "logback-core" % logbackVersion
  val loggingDeps = Seq(slf4j, logBackClassic, logBackCore)
  val wicket = "org.apache.wicket" % "wicket" % wicketVersion
  val wicketExt = "org.apache.wicket" % "wicket-extensions" % wicketVersion
  val jettyServer = "org.eclipse.jetty" % "jetty-server" % jettyVersion
  val jettyServlet = "org.eclipse.jetty" % "jetty-servlet" % jettyVersion
  val webDeps = Seq(wicket, wicketExt, jettyServer, jettyServlet)
}