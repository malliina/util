import Dependencies._
import com.github.siasia.WebPlugin.webSettings
import com.github.siasia.PluginKeys._
import com.typesafe.packager.PackagerPlugin
import sbt.Keys._
import sbt.PlayProject._
import sbt._
import cloudbees.Plugin.{CloudBees, cloudBeesSettings}
import com.mle.util.{Util => MyUtil}

/**
 * @author Mle
 */

object GitBuild extends Build {

  override lazy val settings = super.settings ++ org.sbtidea.SbtIdeaPlugin.ideaSettings

  val commonSettings = Defaults.defaultSettings ++ Seq(
    scalaVersion := "2.9.2",
    version := "0.3-SNAPSHOT",
    retrieveManaged := true,
    publishTo := Some(Resolver.url("my-sbt-releases", new URL("http://xxx/artifactory/my-sbt-releases/"))(Resolver.ivyStylePatterns)),
    publishMavenStyle := false,
    credentials += Credentials(Path.userHome / ".sbt" / "credentials.txt")
  )
  val beesConfig = MyUtil.props((Path.userHome / ".bees" / "bees.config").toString)
  lazy val wicketSettings = commonSettings ++
    webSettings ++
    PackagerPlugin.packagerSettings ++
    Packaging.newSettings ++
    NativePackaging.defaultNativeProject
  lazy val parent = Project("parent", file("."))
  lazy val util = myProject("common-util")
    .settings(libraryDependencies ++= loggingDeps ++ Seq(commonsIO, scalaTest))
  lazy val utilActor = myProject("util-actor")
    .dependsOn(util)
  lazy val play = PlayProject("playapp", path = file("playapp"), applicationVersion = "0.1", dependencies = Nil, mainLang = SCALA)
    .dependsOn(util, utilActor)
  lazy val wicket = Project("wicket", file("wicket"), settings = wicketSettings)
    .dependsOn(util, utilActor, rmi)
    .settings(cloudBeesSettings: _*)
    .settings(
    libraryDependencies ++= webDeps ++ wiQuery,
    CloudBees.applicationId := Some("wicket"),
    CloudBees.apiKey := Some(beesConfig("bees.api.key")),
    CloudBees.apiSecret := Some(beesConfig("bees.api.secret")),
    CloudBees.username := Some(beesConfig("bees.project.app.domain"))  ,
    webappResources in Compile <+= (sourceDirectory in Runtime)(sd => sd / "resources" / "publicweb")
  )
  lazy val rmi = myProject("util-rmi")
    .dependsOn(util)
  lazy val auth = myProject("util-auth")
    .dependsOn(util)
    .settings(libraryDependencies ++= Seq(tomcatJdbc, mysql))
  //  IzPack.variables in IzPack.Config <+= name {
  //    name => ("projectName", "My test project")
  //  }
  //  IzPack.variables in IzPack.Config +=("author", "Michael Skogberg")
  def myProject(id: String) = Project(id, file(id), settings = commonSettings)
}