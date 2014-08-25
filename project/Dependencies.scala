import sbt._

/**
 * @author Mle
 */

object Dependencies {
  val scalaTest = "org.scalatest" %% "scalatest" % "2.2.2" % "test"
  val slf4j = "org.slf4j" % "slf4j-api" % "1.7.7"
  val logBackClassic = "ch.qos.logback" % "logback-classic" % "1.1.2"
  val logBackCore = "ch.qos.logback" % "logback-core" % "1.1.2"
  val loggingDeps = Seq(slf4j, logBackClassic, logBackCore)
  val commonsIO = "commons-io" % "commons-io" % "2.4"
  val commonsCodec = "commons-codec" % "commons-codec" % "1.9"
  val mysql = "mysql" % "mysql-connector-java" % "5.1.29"
  val tomcatJdbc = "org.apache.tomcat" % "tomcat-jdbc" % "7.0.52"
  val boneCp = "com.jolbox" % "bonecp" % "0.7.1.RELEASE"
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.3.5"
  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % "2.3.5" % "test"
  val azureApi = "com.microsoft.windowsazure" % "microsoft-windowsazure-api" % "0.4.6"
  val utilBase = "com.github.malliina" %% "util-base" % "0.2.0"
}