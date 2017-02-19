import sbt.{toGroupID, Test}

object Dependencies {
  val scalaTest = "org.scalatest" %% "scalatest" % "2.2.6" % Test
  val slf4j = "org.slf4j" % "slf4j-api" % "1.7.23"
  val logBackClassic = "ch.qos.logback" % "logback-classic" % "1.2.1"
  val logBackCore = "ch.qos.logback" % "logback-core" % "1.2.1"
  val loggingDeps = Seq(slf4j, logBackClassic, logBackCore)
  val commonsIO = "commons-io" % "commons-io" % "2.4"
  val commonsCodec = "commons-codec" % "commons-codec" % "1.10"
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.4.17"
  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % "2.4.17" % Test
  val azureStorage = "com.microsoft.azure" % "azure-storage" % "5.0.0"
  val utilBase = "com.malliina" %% "util-base" % "1.0.4"
  val ahc = "org.asynchttpclient" % "async-http-client" % "2.0.29"
}
