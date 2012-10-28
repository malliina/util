import sbt._

/**
 * @author Mle
 */

object Dependencies {
  val logbackVersion = "[1.0.6,)"
  val jettyVersion = "8.1.3.v20120416"
  val wicketVersion = "[6.0.0,)"
  val slf4j = "org.slf4j" % "slf4j-api" % "[1.6.6,)"
  val logBackClassic = "ch.qos.logback" % "logback-classic" % logbackVersion
  val logBackCore = "ch.qos.logback" % "logback-core" % logbackVersion
  val loggingDeps = Seq(slf4j, logBackClassic, logBackCore)
  val jettyHack = "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" artifacts Artifact("javax.servlet", "jar", "jar")
  val jettyServer = "org.eclipse.jetty" % "jetty-server" % jettyVersion artifacts Artifact("jetty-server", "jar", "jar")
  val jettyServlet = "org.eclipse.jetty" % "jetty-servlet" % jettyVersion artifacts Artifact("jetty-servlet", "jar", "jar")
  val jettyWebSocket = "org.eclipse.jetty" % "jetty-websocket" % jettyVersion artifacts Artifact("jetty-websocket", "jar", "jar")
  val wicket = "org.apache.wicket" % "wicket-core" % wicketVersion
  val wicketExt = "org.apache.wicket" % "wicket-extensions" % wicketVersion
  val wicketWebSockets = "org.apache.wicket" % "wicket-native-websocket-jetty" % "[0.2,)"
  val wicketAtmosphere = "org.apache.wicket" % "wicket-atmosphere" % "[0.3,)"
  val bootstrap = "org.apache.wicket" % "wicket-bootstrap" % "[0.2,)"
  val bootstrap2 = "de.agilecoders.wicket" % "bootstrap" % "[0.6.1,)"
  val warDep = "org.mortbay.jetty" % "jetty" % "6.1.22" % "container"
  val webDeps = Seq(wicket, wicketExt, wicketWebSockets, wicketAtmosphere, bootstrap2, warDep, jettyHack, jettyServer, jettyServlet, jettyWebSocket)
  val wiQueryCore = "org.odlabs.wiquery" % "wiquery-core" % wicketVersion
  val wiQueryUi = "org.odlabs.wiquery" % "wiquery-jquery-ui" % wicketVersion
  val wiQuery = Seq(wiQueryCore, wiQueryUi)
  val commonsIO = "commons-io" % "commons-io" % "2.4"
  val hashing = "commons-codec" % "commons-codec" % "1.7"
  val scalaTest = "org.scalatest" %% "scalatest" % "[1.8,)" % "test"
  val mysql = "mysql" % "mysql-connector-java" % "[5.1.21,)"
  val tomcatJdbc = "org.apache.tomcat" % "tomcat-jdbc" % "[7.0.30,)"
  val boneCp = "com.jolbox" % "bonecp" % "[0.7.1.RELEASE,)"
  val jerkson = "io.backchat.jerkson" %% "jerkson" % "[0.7.0,)"
}