import com.malliina.sbtutils.SbtUtils
import com.malliina.sbtutils.SbtUtils.{developerName, gitUserName}

val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val slf4j = "org.slf4j" % "slf4j-api" % "1.7.25"
val logBackClassic = "ch.qos.logback" % "logback-classic" % "1.2.3"
val logBackCore = "ch.qos.logback" % "logback-core" % "1.2.3"
val loggingDeps = Seq(slf4j, logBackClassic, logBackCore)
val commonsIO = "commons-io" % "commons-io" % "2.4"
val commonsCodec = "commons-codec" % "commons-codec" % "1.10"
val azureStorage = "com.microsoft.azure" % "azure-storage" % "5.0.0"
val utilBase = "com.malliina" %% "util-base" % "1.3.2"
val httpClient = "org.apache.httpcomponents" % "httpasyncclient" % "4.1.3"

lazy val parent = project.in(file("."))
  .settings(rootSettings: _*)
  .aggregate(util, actor, rmi)

lazy val util = testableProject("util", deps = Seq(commonsIO, commonsCodec, utilBase, httpClient) ++ loggingDeps)
lazy val actor = utilProject("util-actor")
  .settings(extraActorSettings)
lazy val rmi = utilProject("util-rmi")
lazy val utilAzure = utilProject("util-azure", deps = Seq(azureStorage))

lazy val mavenSettings = SbtUtils.mavenSettings ++ baseSettings

def trivialSettings = Seq(
  organization := s"com.${gitUserName.value}",
  gitUserName := "malliina",
  developerName := "Michael Skogberg",
  scalaVersion := "2.12.4",
  crossScalaVersions := Seq("2.10.6", "2.11.11", scalaVersion.value),
  releaseCrossBuild := true,
  resolvers ++= Seq(
    "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases/",
    "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/",
    Resolver.jcenterRepo
  ),
  scalacOptions ++= Seq("-Xlint", "-feature")
)
def baseSettings = trivialSettings ++ Seq(
  // system properties seem to have no effect in tests,
  // causing e.g. tests requiring javax.net.ssl.keyStore props to fail
  // ... unless fork is true
  fork in Test := true
)

def extraActorSettings = Seq(
  libraryDependencies ++= {
    val actorVersion = CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, minor)) if minor >= 11 => "2.5.3"
      case _ => "2.3.16"
    }
    Seq(
      "com.typesafe.akka" %% "akka-actor" % actorVersion,
      "com.typesafe.akka" %% "akka-testkit" % actorVersion % Test
    )
  }
)

def rootSettings = trivialSettings ++ Seq(
  publishArtifact := false,
  publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo")))
)

def utilProject(id: String, deps: Seq[ModuleID] = Seq.empty) =
  testableProject(id, deps).dependsOn(util)

def testableProject(id: String, deps: Seq[ModuleID] = Seq.empty) =
  baseProject(id, deps).settings(mavenSettings: _*)

def baseProject(id: String, deps: Seq[ModuleID]) =
  Project(id, file(id))
    .settings(libraryDependencies ++= deps ++ Seq(scalaTest))