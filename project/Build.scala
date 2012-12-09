//import com.mle.sbt.GenericPackaging
//import com.mle.sbt.NativePackaging
//import com.mle.sbt.Packaging
//import com.mle.sbt.WindowsPlugin

import com.mle.sbt.unix.{UnixZipPackaging, LinuxPackaging}
import com.mle.sbt.win.WindowsPlugin
import com.github.siasia.WebPlugin.webSettings
import com.github.siasia.PluginKeys._
import com.typesafe.packager.PackagerPlugin
import sbt.Keys._
import sbt.PlayProject._
import sbt._
import cloudbees.Plugin.{CloudBees, cloudBeesSettings}
import com.mle.util.{Util => MyUtil}
import Dependencies._

/**
 * @author Mle
 */

object GitBuild extends Build {
  // hack to make sbt-idea and play 2.1 plugins work
  override lazy val settings = super.settings ++ org.sbtidea.SbtIdeaPlugin.ideaSettings
  val credentialPath = Path.userHome / ".sbt" / "credentials.txt"
  val credentialSettings =
    if (credentialPath.exists())
      Seq(credentials += Credentials(credentialPath))
    else Seq.empty
  val commonSettings = Defaults.defaultSettings ++ Seq(
    scalaVersion := "2.9.2",
    version := "0.3-SNAPSHOT",
    retrieveManaged := true,
    publishTo := Some(Resolver.url("my-sbt-releases", new URL("http://xxx/artifactory/my-sbt-releases/"))(Resolver.ivyStylePatterns)),
    publishMavenStyle := false,
    // system properties seem to have no effect in tests,
    // causing e.g. tests requiring javax.net.ssl.keyStore props to fail
    // ... unless fork is true
    sbt.Keys.fork in Test := true,
    // the jars of modules depended on are not included unless this is true
    exportJars := true
  ) ++ credentialSettings

  def myWebSettings: Seq[Setting[_]] = webSettings ++ Seq(libraryDependencies ++= webDeps, webappResources in Compile <+= (sourceDirectory in Runtime)(sd => sd / "resources" / "publicweb"))

  val beesConfig = MyUtil.optionally(
    MyUtil.props((Path.userHome / ".bees" / "bees.config").toString)
  ).getOrElse(Map.empty)
  lazy val parent = Project("parent", file("."))
  lazy val util = myProject("util")
    .settings(libraryDependencies ++= loggingDeps ++ Seq(commonsIO, scalaTest, jerkson))
  lazy val utilActor = basicProject("util-actor")
  lazy val utilJdbc = basicProject("util-jdbc")
    // Kids, watch and learn. test->test means this module's tests depend on tests in module auth
    .dependsOn(auth % "compile->compile;test->test")
    .settings(libraryDependencies ++= Seq(tomcatJdbc, boneCp, mysql))
  lazy val utilWeb = basicProject("util-web")
    .settings(libraryDependencies ++= webDeps)
    .settings(webSettings: _*)
  lazy val rmi = basicProject("util-rmi")
  lazy val auth = basicProject("util-auth")
    .settings(libraryDependencies ++= Seq(hashing))
  lazy val play = PlayProject("playapp", path = file("playapp"), applicationVersion = "0.1", dependencies = Nil, mainLang = SCALA)
    .dependsOn(util, utilActor, utilJdbc)
  lazy val wicketSettings: Seq[Setting[_]] = PackagerPlugin.packagerSettings ++
    WindowsPlugin.windowsSettings ++
    LinuxPackaging.rpmSettings ++
    LinuxPackaging.debianSettings ++
    UnixZipPackaging.unixZipSettings
  lazy val wicket = webProject("wicket")
    .dependsOn(utilActor, rmi, auth, utilJdbc)
    .settings(wicketSettings: _*)
    .settings(cloudBeesSettings: _*)
    .settings(
    libraryDependencies ++= wiQuery ++ Seq(jerkson),
    CloudBees.applicationId := Some("wicket"),
    CloudBees.apiKey := beesConfig get "bees.api.key",
    CloudBees.apiSecret := beesConfig get "bees.api.secret",
    CloudBees.username := beesConfig get "bees.project.app.domain",
    webappResources in Compile <+= (sourceDirectory in Runtime)(sd => sd / "resources" / "publicweb"),
    mainClass := Some("com.mle.wicket.WicketStart")
  )
  lazy val homePage = webProject("homepage")
    .settings(cloudBeesSettings: _*)
    .settings(com.typesafe.sbt.SbtStartScript.startScriptForClassesSettings: _*)
    .settings(CloudBees.applicationId := Some("home"),
    CloudBees.apiKey := beesConfig get "bees.api.key",
    CloudBees.apiSecret := beesConfig get "bees.api.secret",
    CloudBees.username := beesConfig get "bees.project.app.domain",
    // TODO DRY but test with .war packaging; myWebSettings doesn't cut it
    webappResources in Compile <+= (sourceDirectory in Runtime)(sd => sd / "resources" / "publicweb")
  )

  def myProject(id: String) = Project(id, file(id), settings = commonSettings)

  def basicProject(id: String) = myProject(id).dependsOn(util).settings(libraryDependencies += scalaTest)

  def webProject(id: String) = basicProject(id)
    .dependsOn(utilWeb)
    .settings(myWebSettings: _*)
}