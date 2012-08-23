import sbt._
import sbt.Keys._
import com.typesafe.packager.PackagerPlugin._
import com.typesafe.packager.{PackagerPlugin, linux, debian, rpm, windows}

/**
 * @author Mle
 */

object GitBuild extends Build {
  lazy val parent = Project("parent", file("."))
  lazy val util = Project("common-util", file("common-util"), settings = mySettings)
  lazy val test = Project("test", file("test"), settings = mySettings ++ PackagerPlugin.packagerSettings).dependsOn(util).settings(
    // http://lintian.debian.org/tags/maintainer-address-missing.html
    linux.Keys.maintainer := "Michael Skogberg <malliina123@ǧmail.com>",
    linux.Keys.packageSummary := "Test package summary",
    linux.Keys.packageDescription := "This is the description of the test package",
    name := "test",
    debian.Keys.version := "0.1",
    rpm.Keys.rpmRelease := "Release 0.1 for RPM",
    rpm.Keys.rpmVendor := "kingmichael",
    windows.Keys.wixFile := new File("doesnotexist"),
    linux.Keys.linuxPackageMappings <+= baseDirectory map {
      bd => (packageMapping((bd / "dist" / "app.txt") -> "/opt/test/app.txt") withUser "root" withPerms "0644")
    },
    debian.Keys.linuxPackageMappings <+= (baseDirectory, name) map {
      // http://lintian.debian.org/tags/no-copyright-file.html
      (bd, pkgName) => (packageMapping((bd / "dist" / "copyright") -> ("/usr/share/doc/" + pkgName + "/copyright")) withUser "root" withPerms "0644")
    },
    debian.Keys.debianPackageDependencies in Debian ++= Seq("wget")
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