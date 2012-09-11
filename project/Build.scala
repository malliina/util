import Dependencies._
import com.github.siasia.WebPlugin.webSettings
import com.typesafe.packager.PackagerPlugin._
import com.typesafe.packager.{PackagerPlugin, linux, debian, rpm, windows}
import sbt.Keys._
import sbt._
import PlayProject._

/**
 * @author Mle
 */

object GitBuild extends Build {

  override def settings = super.settings ++ org.sbtidea.SbtIdeaPlugin.ideaSettings

  val commonSettings = Defaults.defaultSettings ++ Seq(
    scalaVersion := "2.9.2",
    version := "0.1-SNAPSHOT",
    //      resolvers := additionalRepos,
    exportJars := true,
    retrieveManaged := true,
    publishTo := Some(Resolver.url("sbt-plugin-releases", new URL("http://xxx/artifactory/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)),
    publishMavenStyle := false,
    credentials += Credentials(Path.userHome / ".sbt" / "credentials.txt")
  )
  val wicketSettings = commonSettings ++
    Packaging.newSettings ++
    webSettings ++
    PackagerPlugin.packagerSettings ++
    NativePackaging.defaultPackageSettings
  val playDeps = Nil
  lazy val parent = Project("parent", file("."))
  lazy val play = PlayProject("playapp", applicationVersion = "0.1", dependencies = playDeps, path = file("playapp"), mainLang = SCALA)
  lazy val util = Project("common-util", file("common-util"), settings = commonSettings)
    .settings(libraryDependencies ++= loggingDeps)
  lazy val wicket = Project("wicket", file("wicket"), settings = wicketSettings)
    .dependsOn(util)
    .settings(libraryDependencies ++= webDeps ++ wiQuery)

  //  IzPack.variables in IzPack.Config <+= name {
  //    name => ("projectName", "My test project")
  //  }
  //  IzPack.variables in IzPack.Config +=("author", "Michael Skogberg")
}