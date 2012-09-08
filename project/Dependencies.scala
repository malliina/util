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
  val wicketWebSockets = "org.apache.wicket" % "wicket-native-websocket-jetty" % "0.2"
  val wicketAtmosphere = "org.apache.wicket" % "wicket-atmosphere" % "0.3"
  val jettyServer = "org.eclipse.jetty" % "jetty-server" % jettyVersion
  val jettyServlet = "org.eclipse.jetty" % "jetty-servlet" % jettyVersion
  val jettyWebSocket = "org.eclipse.jetty" % "jetty-websocket" % jettyVersion
  val webDeps = Seq(wicket, wicketExt, wicketWebSockets, wicketAtmosphere, jettyServer, jettyServlet, jettyWebSocket)
  val wiQueryCore = "org.odlabs.wiquery" % "wiquery-core" % "6.0.0"
  val wiQueryUi = "org.odlabs.wiquery" % "wiquery-jquery-ui" % "6.0.0"
  val wiQuery = Seq(wiQueryCore, wiQueryUi)
}