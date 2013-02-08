import com.mle.sbt.unix.{UnixZipPackaging, LinuxPackaging}
import com.mle.sbt.win.WindowsPlugin
import com.github.siasia.WebPlugin.webSettings
import com.github.siasia.PluginKeys._
import com.typesafe.packager.{windows, PackagerPlugin}
import sbt.Keys._
import sbt._
import cloudbees.Plugin.{CloudBees, cloudBeesSettings}
import com.mle.util.{Util => MyUtil}
import Dependencies._

/**
 * @author Mle
 */

object GitBuild extends Build {
  val commonSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.mle",
    version := "0.68-SNAPSHOT",
    scalaVersion := "2.10.0",
    retrieveManaged := false,
    publishTo := Some(Resolver.url("my-sbt-releases", new URL("http://xxx/artifactory/my-sbt-releases/"))(Resolver.ivyStylePatterns)),
    publishMavenStyle := false,
    // system properties seem to have no effect in tests,
    // causing e.g. tests requiring javax.net.ssl.keyStore props to fail
    // ... unless fork is true
    sbt.Keys.fork in Test := true,
    // the jars of modules depended on are not included unless this is true
    exportJars := true
  )

  lazy val parent = Project("parent", file("."))
    .aggregate(util, actor, jdbc, utilWeb, rmi, auth)
  // last 2.9.2 is 0.67-SNAPSHOT
  // 0.67-SNAPSHOT is an sbt plugin
  lazy val util = myProject("util")
    .settings(
    sbtPlugin := false,
    libraryDependencies ++= loggingDeps ++ Seq(commonsIO, scalaTest),
    crossScalaVersions := Seq("2.9.2", "2.10")
  )
  lazy val actor = basicProject("util-actor")
    .settings(libraryDependencies ++= Seq(akkaActor, akkaTestKit))
  lazy val jdbc = basicProject("util-jdbc")
    // Kids, watch and learn. auth % "test->test" means this module's tests depend on tests in module auth
    .dependsOn(auth % "compile->compile;test->test")
    .settings(libraryDependencies ++= Seq(tomcatJdbc, boneCp, mysql))
  lazy val utilWeb = basicProject("util-web")
    .settings(libraryDependencies ++= webDeps)
  lazy val rmi = basicProject("util-rmi")
  lazy val auth = basicProject("util-auth")
    .settings(libraryDependencies ++= Seq(hashing))

  def myProject(id: String, customSettings: Seq[Project.Setting[_]] = Seq.empty) = Project(id, file(id), settings = commonSettings ++ customSettings)

  def basicProject(id: String, customSettings: Seq[Project.Setting[_]] = Seq.empty) = myProject(id, customSettings)
    .dependsOn(util)
    .settings(libraryDependencies += scalaTest)
}