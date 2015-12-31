import sbt._

/**
 * @author Mle
 */
object Dependencies {
  val scalaTest = "org.scalatest" %% "scalatest" % "2.2.5" % "test"
  val slf4j = "org.slf4j" % "slf4j-api" % "1.7.12"
  val logBackClassic = "ch.qos.logback" % "logback-classic" % "1.1.3"
  val logBackCore = "ch.qos.logback" % "logback-core" % "1.1.3"
  val loggingDeps = Seq(slf4j, logBackClassic, logBackCore)
  val commonsIO = "commons-io" % "commons-io" % "2.4"
  val commonsCodec = "commons-codec" % "commons-codec" % "1.10"
  val mysql = "mysql" % "mysql-connector-java" % "5.1.36"
  val tomcatJdbc = "org.apache.tomcat" % "tomcat-jdbc" % "8.0.26"
  val boneCp = "com.jolbox" % "bonecp" % "0.8.0.RELEASE"
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.3.13"
  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % "2.3.13" % "test"
  val azureApi = "com.microsoft.windowsazure" % "microsoft-windowsazure-api" % "0.4.6"
  val utilBase = "com.malliina" %% "util-base" % "0.9.0"
  val ningHttp = "com.ning" % "async-http-client" % "1.9.31"
}
