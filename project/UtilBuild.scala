import Dependencies._
import com.mle.sbtutils.SbtUtils
import sbt.Keys._
import sbt._

/**
 * @author Mle
 */

object UtilBuild extends Build {
  val releaseVersion = "1.6.1"
  val snapshotVersion = "1.6.1-SNAPSHOT"
  val latestUtil = "com.github.malliina" %% "util" % releaseVersion
  val stableUtil = "com.github.malliina" %% "util" % "1.5.0"

  lazy val util = testableProject("util", deps = Seq(commonsIO, commonsCodec, utilBase, ningHttp, playJson) ++ loggingDeps)
    .settings(version := releaseVersion)
  lazy val actor = utilProject("util-actor", deps = Seq(akkaActor, akkaTestKit))
    .settings(version := releaseVersion)
  lazy val rmi = utilProject("util-rmi")
    .settings(version := releaseVersion)
  lazy val jdbc = utilProject("util-jdbc", deps = Seq(tomcatJdbc, boneCp, mysql))
    // Kids, watch and learn. auth % "test->test" means this module's tests depend on tests in module auth
    .dependsOn(auth % "compile->compile;test->test")
  lazy val auth = utilProject("util-auth", deps = Seq(commonsCodec))
  lazy val utilAzure = testableProject("util-azure", deps = Seq(azureApi, latestUtil))
    .settings(version := releaseVersion)

  val commonSettings = SbtUtils.publishSettings ++ Seq(
    version := releaseVersion,
    SbtUtils.gitUserName := "malliina",
    SbtUtils.developerName := "Michael Skogberg",
    scalaVersion := "2.11.5",
    crossScalaVersions := Seq("2.11.5", "2.10.4"),
    retrieveManaged := false,
    // system properties seem to have no effect in tests,
    // causing e.g. tests requiring javax.net.ssl.keyStore props to fail
    // ... unless fork is true
    sbt.Keys.fork in Test := true,
    exportJars := true,
    resolvers ++= Seq(
      "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases/",
      "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/"),
    scalacOptions ++= Seq("-Xlint", "-feature")
  )

  lazy val parent = Project("parent", file("."), settings = commonSettings)
    .aggregate(util, actor, jdbc, rmi, auth)

  def testableProject(id: String, deps: Seq[ModuleID] = Seq.empty) =
    Project(id, file(id)).settings(
      libraryDependencies ++= deps ++ Seq(scalaTest)
    ).settings(commonSettings: _*)

  def utilProject(id: String, deps: Seq[ModuleID] = Seq.empty) = testableProject(id, deps ++ Seq(stableUtil))
}