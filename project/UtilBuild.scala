import Dependencies._
import com.mle.sbtutils.SbtUtils.{gitUserName, developerName}
import sbt.Keys._
import sbt._

/**
 * @author Mle
 */

object UtilBuild extends Build {
  val releaseVersion = "2.0.0"

  lazy val parent = Project("parent", file("."), settings = commonSettings)
    .aggregate(util, actor, jdbc, rmi, auth)

  lazy val util = testableProject("util", deps = Seq(commonsIO, commonsCodec, utilBase, ningHttp) ++ loggingDeps)
  lazy val actor = utilProject("util-actor", deps = Seq(akkaActor, akkaTestKit))
  lazy val rmi = utilProject("util-rmi")
  lazy val jdbc = utilProject("util-jdbc", deps = Seq(tomcatJdbc, boneCp, mysql))
    // Kids, watch and learn. auth % "test->test" means this module's tests depend on tests in module auth
    .dependsOn(auth % "compile->compile;test->test")
  lazy val auth = utilProject("util-auth", deps = Seq(commonsCodec))
  lazy val utilAzure = testableProject("util-azure", deps = Seq(azureApi)).dependsOn(util)

  val commonSettings = Seq(
    organization := s"com.github.${gitUserName.value}",
    version := releaseVersion,
    gitUserName := "malliina",
    developerName := "Michael Skogberg",
    scalaVersion := "2.11.7",
    crossScalaVersions := Seq(scalaVersion.value, "2.10.4"),
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
    Project(id, file(id)).enablePlugins(bintray.BintrayPlugin).settings(
      libraryDependencies ++= deps ++ Seq(scalaTest)
    ).settings(commonSettings: _*)

  def utilProject(id: String, deps: Seq[ModuleID] = Seq.empty) = testableProject(id, deps).dependsOn(util)
}
