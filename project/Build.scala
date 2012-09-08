import Dependencies._
import com.typesafe.packager.PackagerPlugin._
import com.typesafe.packager.{PackagerPlugin, linux, debian, rpm, windows}
import sbt.Keys._
import sbt._

/**
 * @author Mle
 */

object GitBuild extends Build {
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
  lazy val parent = Project("parent", file("."))
  lazy val util = Project("common-util", file("common-util"), settings = mySettings(loggingDeps))
  lazy val test = Project("test", file("test"), settings = mySettings() ++ PackagerPlugin.packagerSettings).dependsOn(util).settings(
    classDirectory <<= (baseDirectory)(b => b / "WEB-INF" / "classes"),
    libraryDependencies ++= webDeps ++ wiQuery,
    // http://lintian.debian.org/tags/maintainer-address-missing.html
    linux.Keys.maintainer := "Michael Skogberg <malliina123@gmail.com>",
    linux.Keys.packageSummary := "This is a summary of package test",
    linux.Keys.packageDescription := "This is the description of package test.",
    name := "test",
    debian.Keys.version := "0.1",
    // Tag takes single token only
    rpm.Keys.rpmRelease := "0.1",
    rpm.Keys.rpmVendor := "kingmichael",
    rpm.Keys.rpmLicense := Some("You have the right to remain silent"),
    windows.Keys.wixFile := new File("doesnotexist"),
    debian.Keys.linuxPackageMappings in Debian <+= (baseDirectory, name) map (
      // http://lintian.debian.org/tags/no-copyright-file.html
      (bd, pkgName) => (packageMapping((bd / "dist" / "copyright") -> ("/usr/share/doc/" + pkgName + "/copyright")) withUser "root" withPerms "0644")
      ),
    debian.Keys.linuxPackageMappings in Debian <+= (baseDirectory, name) map (
      // http://lintian.debian.org/tags/changelog-file-missing-in-native-package.html
      (bd, pkgName) => (packageMapping((bd / "dist" / "copyright") -> ("/usr/share/doc/" + pkgName + "/changelog.gz")) withUser "root" withPerms "0644" gzipped) asDocs()
      ),
    linux.Keys.linuxPackageMappings <+= (baseDirectory) map (
      (bd: File) => (packageMapping((bd / "dist" / "app.txt") -> "/opt/test/app.txt") withUser "root" withPerms "0644")
      ),
    debian.Keys.debianPackageDependencies in Debian ++= Seq("wget")
  )

  def mySettings(deps: Seq[ModuleID] = Seq.empty) = commonSettings ++ Seq(libraryDependencies ++= deps)

  //  IzPack.variables in IzPack.Config <+= name {
  //    name => ("projectName", "My test project")
  //  }
  //  IzPack.variables in IzPack.Config +=("author", "Michael Skogberg")
}