import Dependencies._
import com.malliina.sbtutils.SbtUtils.{gitUserName, developerName}
import sbt.Keys._
import sbt._

object UtilBuild extends Build {
  val releaseVersion = "2.4.1"

  lazy val parent = Project("parent", file("."), settings = commonSettings)
    .aggregate(util, actor, rmi, auth)

  lazy val util = testableProject("util", deps = Seq(commonsIO, commonsCodec, utilBase, ahc) ++ loggingDeps)
  lazy val actor = utilProject("util-actor", deps = Seq(akkaActor, akkaTestKit))
  lazy val rmi = utilProject("util-rmi")
    // Kids, watch and learn. auth % "test->test" means this module's tests depend on tests in module auth
    .dependsOn(auth % "compile->compile;test->test")
  lazy val auth = utilProject("util-auth", deps = Seq(commonsCodec))
  lazy val utilAzure = baseProject("util-azure", deps = Seq(azureApi))
    .settings(azureSettings: _*)
    .dependsOn(util)

  val commonSettings = baseSettings ++ Seq(
    version := releaseVersion
  )

  val azureSettings = baseSettings ++ Seq(
    version := "2.2.3"
  )

  def baseSettings = Seq(
    organization := s"com.${gitUserName.value}",
    gitUserName := "malliina",
    developerName := "Michael Skogberg",
    scalaVersion := "2.11.7",
    crossScalaVersions := Seq(scalaVersion.value, "2.10.6"),
    retrieveManaged := false,
    // system properties seem to have no effect in tests,
    // causing e.g. tests requiring javax.net.ssl.keyStore props to fail
    // ... unless fork is true
    sbt.Keys.fork in Test := true,
    exportJars := true,
    resolvers ++= Seq(
      "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases/",
      "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/",
      sbt.Resolver.jcenterRepo
    ),
    //      "Bintray malliina" at "http://dl.bintray.com/malliina/maven"),
    scalacOptions ++= Seq("-Xlint", "-feature"),
    updateOptions := updateOptions.value.withCachedResolution(true),
    licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
  )

  def testableProject(id: String, deps: Seq[ModuleID] = Seq.empty) =
    baseProject(id, deps).settings(commonSettings: _*)

  def baseProject(id: String, deps: Seq[ModuleID]) =
    Project(id, file(id)).enablePlugins(bintray.BintrayPlugin)
      .settings(libraryDependencies ++= deps ++ Seq(scalaTest))

  def utilProject(id: String, deps: Seq[ModuleID] = Seq.empty) =
    testableProject(id, deps).dependsOn(util)
}
