import Dependencies._
import com.github.siasia.WebPlugin.webSettings
import com.typesafe.packager.PackagerPlugin
import sbt.Keys._
import sbt.PlayProject._
import sbt._

/**
 * @author Mle
 */

object GitBuild extends Build {

  override lazy val settings = super.settings ++ org.sbtidea.SbtIdeaPlugin.ideaSettings

  val commonSettings = Defaults.defaultSettings ++ Seq(
    scalaVersion := "2.9.2",
    version := "0.1-SNAPSHOT",
    retrieveManaged := true,
    publishTo := Some(Resolver.url("my-sbt-releases", new URL("http://xxx/artifactory/my-sbt-releases/"))(Resolver.ivyStylePatterns)),
    publishMavenStyle := false,
    credentials += Credentials(Path.userHome / ".sbt" / "credentials.txt")
  )
  lazy val wicketSettings = commonSettings ++
    webSettings ++
    PackagerPlugin.packagerSettings ++
    Packaging.newSettings ++
    NativePackaging.defaultNativeProject
  lazy val parent = Project("parent", file("."))
  lazy val util = myProject("common-util")
    .settings(libraryDependencies ++= loggingDeps ++ Seq(commonsIO))
  lazy val utilActor = myProject("util-actor")
    .dependsOn(util)
  lazy val play = PlayProject("playapp", path = file("playapp"), applicationVersion = "0.1", dependencies = Nil, mainLang = SCALA)
    .dependsOn(util, utilActor)
  lazy val wicket = Project("wicket", file("wicket"), settings = wicketSettings)
    .dependsOn(util, utilActor, rmi)
    .settings(libraryDependencies ++= webDeps ++ wiQuery)
  lazy val rmi = myProject("util-rmi")
    .dependsOn(util)
  lazy val auth = myProject("util-auth")
    .dependsOn(util)

  //  IzPack.variables in IzPack.Config <+= name {
  //    name => ("projectName", "My test project")
  //  }
  //  IzPack.variables in IzPack.Config +=("author", "Michael Skogberg")
  def myProject(id: String) = Project(id, file(id), settings = commonSettings)
}