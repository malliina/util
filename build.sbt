val scalaTest = "org.scalatest" %% "scalatest" % "3.0.7" % Test
val slf4j = "org.slf4j" % "slf4j-api" % "1.7.26"
val logBackClassic = "ch.qos.logback" % "logback-classic" % "1.2.3"
val logBackCore = "ch.qos.logback" % "logback-core" % "1.2.3"
val loggingDeps = Seq(slf4j, logBackClassic, logBackCore)
val commonsIO = "commons-io" % "commons-io" % "2.6"
val commonsCodec = "commons-codec" % "commons-codec" % "1.12"
val azureStorage = "com.microsoft.azure" % "azure-storage" % "5.0.0"
val utilBase = "com.malliina" %% "util-base" % "1.9.0"

lazy val utilRoot = project.in(file("."))
  .settings(rootSettings: _*)
  .aggregate(util, actor, rmi)

lazy val util = testableProject("util", deps = Seq(commonsIO, commonsCodec, utilBase) ++ loggingDeps)
lazy val actor = utilProject("util-actor")
  .settings(extraActorSettings)
lazy val rmi = utilProject("util-rmi")
lazy val utilAzure = utilProject("util-azure", deps = Seq(azureStorage))

lazy val mavenSettings = baseSettings

def trivialSettings = Seq(
  organization := s"com.${gitUserName.value}",
  gitUserName := "malliina",
  developerName := "Michael Skogberg",
  scalaVersion := "2.12.8",
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
    val actorVersion = "2.5.21"
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
  baseProject(id, deps).enablePlugins(MavenCentralPlugin).settings(baseSettings: _*)

def baseProject(id: String, deps: Seq[ModuleID]) =
  Project(id, file(id))
    .settings(libraryDependencies ++= deps ++ Seq(scalaTest))
