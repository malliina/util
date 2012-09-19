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

  override lazy val settings = super.settings

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
  lazy val util = Project("common-util", file("common-util"), settings = commonSettings)
    .settings(libraryDependencies ++= loggingDeps)
  lazy val utilActor = Project("util-actor", file("util-actor"), settings = commonSettings)
    .dependsOn(util)
  lazy val play = PlayProject("playapp", path = file("playapp"), applicationVersion = "0.1", dependencies = playDeps, mainLang = SCALA)
    .dependsOn(util, utilActor)
  lazy val wicket = Project("wicket", file("wicket"), settings = wicketSettings)
    .dependsOn(util, utilActor)
    .settings(libraryDependencies ++= webDeps ++ wiQuery)

  //  IzPack.variables in IzPack.Config <+= name {
  //    name => ("projectName", "My test project")
  //  }
  //  IzPack.variables in IzPack.Config +=("author", "Michael Skogberg")
}