import sbt.toGroupID

object Dependencies {
  val scalaTest = "org.scalatest" %% "scalatest" % "2.2.6" % "test"
  val slf4j = "org.slf4j" % "slf4j-api" % "1.7.12"
  val logBackClassic = "ch.qos.logback" % "logback-classic" % "1.1.3"
  val logBackCore = "ch.qos.logback" % "logback-core" % "1.1.3"
  val loggingDeps = Seq(slf4j, logBackClassic, logBackCore)
  val commonsIO = "commons-io" % "commons-io" % "2.4"
  val commonsCodec = "commons-codec" % "commons-codec" % "1.10"
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.4.4"
  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % "2.4.4" % "test"
  val azureApi = "com.microsoft.windowsazure" % "microsoft-windowsazure-api" % "0.4.6"
  val utilBase = "com.malliina" %% "util-base" % "1.0.0"
  val ahc = "org.asynchttpclient" % "async-http-client" % "2.0.0"
}
