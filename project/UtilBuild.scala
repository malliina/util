import Dependencies._
import com.malliina.sbtutils.SbtUtils
import com.malliina.sbtutils.SbtUtils.{developerName, gitUserName}
import sbt.Keys._
import sbt._

object UtilBuild {
  lazy val parent = Project("parent", file("."), settings = rootSettings)
    .aggregate(util, actor, rmi, auth)

  lazy val util = testableProject("util", deps = Seq(commonsIO, commonsCodec, utilBase, ahc) ++ loggingDeps)
  lazy val actor = utilProject("util-actor", deps = Seq(akkaActor, akkaTestKit))
  lazy val rmi = utilProject("util-rmi")
  lazy val auth = utilProject("util-auth", deps = Seq(commonsCodec))
  lazy val utilAzure = utilProject("util-azure", deps = Seq(azureStorage))

  lazy val mavenSettings = SbtUtils.mavenSettings ++ baseSettings

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
    scalacOptions ++= Seq("-Xlint", "-feature")
  )

  def rootSettings = baseSettings ++ Seq(
    publishArtifact := false
  )

  def utilProject(id: String, deps: Seq[ModuleID] = Seq.empty) =
    testableProject(id, deps).dependsOn(util)

  def testableProject(id: String, deps: Seq[ModuleID] = Seq.empty) =
    baseProject(id, deps).settings(mavenSettings: _*)

  def baseProject(id: String, deps: Seq[ModuleID]) =
    Project(id, file(id))
      .settings(libraryDependencies ++= deps ++ Seq(scalaTest))
}
