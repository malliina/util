import Dependencies._
import com.malliina.sbtutils.SbtUtils
import com.malliina.sbtutils.SbtUtils.{developerName, gitUserName}
import sbt.Keys._
import sbt._

object UtilBuild {
  val releaseVersion = "2.5.0"

  lazy val parent = Project("parent", file("."), settings = commonSettings)
    .aggregate(util, actor, rmi, auth)

  lazy val util = testableProject("util", deps = Seq(commonsIO, commonsCodec, utilBase, ahc) ++ loggingDeps)
  lazy val actor = utilProject("util-actor", deps = Seq(akkaActor, akkaTestKit))
  lazy val rmi = utilProject("util-rmi")
    // Kids, watch and learn. auth % "test->test" means this module's tests depend on tests in module auth
    .dependsOn(auth % "compile->compile;test->test")
  lazy val auth = utilProject("util-auth", deps = Seq(commonsCodec))
  lazy val utilAzure = baseProject("util-azure", deps = Seq(azureStorage))
    .settings(azureSettings: _*)
    .dependsOn(util)

  lazy val mavenSettings = SbtUtils.mavenSettings ++ commonSettings

  lazy val commonSettings = baseSettings ++ Seq(
    version := releaseVersion
  )

  lazy val azureSettings = baseSettings ++ Seq(
    version := "2.2.3"
  )

  def baseSettings = Seq(
    organization := s"com.${gitUserName.value}",
    gitUserName := "malliina",
    developerName := "Michael Skogberg",
    scalaVersion := "2.11.8",
    // system properties seem to have no effect in tests,
    // causing e.g. tests requiring javax.net.ssl.keyStore props to fail
    // ... unless fork is true
    fork in Test := true,
    resolvers ++= Seq(
      "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases/",
      "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/",
      Resolver.jcenterRepo
    ),
    //      "Bintray malliina" at "http://dl.bintray.com/malliina/maven"),
    scalacOptions ++= Seq("-Xlint", "-feature")
  )

  def testableProject(id: String, deps: Seq[ModuleID] = Seq.empty) =
    baseProject(id, deps).settings(mavenSettings: _*)

  def baseProject(id: String, deps: Seq[ModuleID]) =
    Project(id, file(id))
      .settings(libraryDependencies ++= deps ++ Seq(scalaTest))

  def utilProject(id: String, deps: Seq[ModuleID] = Seq.empty) =
    testableProject(id, deps).dependsOn(util)
}
