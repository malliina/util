import sbt._
import sbt.Keys._
import com.typesafe.packager.{PackagerPlugin, linux, rpm, windows}

/**
 * @author Mle
 */

object GitBuild extends Build {
  lazy val parent = Project("parent", file("."))
  lazy val util = Project("common-util", file("common-util"), settings = mySettings)
  lazy val test = Project("test", file("test"), settings = mySettings ++ PackagerPlugin.packagerSettings).dependsOn(util).settings(
    linux.Keys.maintainer := "Michael Skogberg",
    linux.Keys.packageSummary := "Test package summary",
    linux.Keys.packageDescription := "Test package description",
    name := "test",
    rpm.Keys.rpmRelease := "Release 0.1 for RPM",
    rpm.Keys.rpmVendor := "King Michael",
    windows.Keys.wixFile := new File("doesnotexist")
  )

  def mySettings = commonSettings

  val commonSettings = Defaults.defaultSettings ++ Seq(
    scalaVersion := "2.9.2",
    version := "0.1-SNAPSHOT",
    //      resolvers := additionalRepos,
    exportJars := true,
    retrieveManaged := true
  )
  //  IzPack.variables in IzPack.Config <+= name {
  //    name => ("projectName", "My test project")
  //  }
  //  IzPack.variables in IzPack.Config +=("author", "Michael Skogberg")
}