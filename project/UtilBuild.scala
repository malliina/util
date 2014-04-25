import sbt.Keys._
import sbt._
import Dependencies._
import com.mle.sbtutils.SbtUtils._

/**
 * @author Mle
 */

object UtilBuild extends Build {
  val releaseVersion = "1.3.1"
  val snapshotVersion = "1.2.1-SNAPSHOT"
  val utilDep2 = "com.github.malliina" %% "util" % releaseVersion

  lazy val util = testableProject("util", deps = Seq(commonsIO, commonsCodec, utilBase) ++ loggingDeps)
    .settings(version := releaseVersion)
  lazy val actor = utilProject("util-actor", deps = Seq(akkaActor, akkaTestKit))
    .settings(version := releaseVersion)
  lazy val rmi = utilProject("util-rmi")
    .settings(version := releaseVersion)
  lazy val jdbc = utilProject("util-jdbc", deps = Seq(tomcatJdbc, boneCp, mysql))
    // Kids, watch and learn. auth % "test->test" means this module's tests depend on tests in module auth
    .dependsOn(auth % "compile->compile;test->test")
  lazy val auth = utilProject("util-auth", deps = Seq(commonsCodec))
  lazy val utilAzure = testableProject("util-azure", deps = Seq(azureApi, utilDep2))
    .settings(version := releaseVersion)

  val commonSettings = Defaults.defaultSettings ++ publishSettings ++ Seq(
    version := snapshotVersion,
    gitUserName := "malliina",
    developerName := "Michael Skogberg",
    scalaVersion := "2.11.0",
    crossScalaVersions := Seq("2.11.0", "2.10.4"),
    retrieveManaged := false,
    // system properties seem to have no effect in tests,
    // causing e.g. tests requiring javax.net.ssl.keyStore props to fail
    // ... unless fork is true
    sbt.Keys.fork in Test := true,
    exportJars := true
  )

  lazy val parent = Project("parent", file("."), settings = commonSettings)
    .aggregate(util, actor, jdbc, rmi, auth)

  def testableProject(id: String, deps: Seq[ModuleID] = Seq.empty) =
    Project(id, file(id), settings = commonSettings).settings(
      libraryDependencies ++= deps ++ Seq(scalaTest)
    )

  def utilProject(id: String, deps: Seq[ModuleID] = Seq.empty) =
    testableProject(id, deps).dependsOn(util)
}