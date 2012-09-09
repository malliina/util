import sbt.Keys._
import sbt._

/**
 * Build definition of the build definition. I don't like build.sbt/plugins.sbt files.
 *
 * This replacement gives better IDE support and a more consistent approach to configuration.
 *
 * @author Mle
 */
object BuildBuild extends Build {
  // "build.sbt" goes here
  override lazy val settings = super.settings ++ Seq(
    scalaVersion := "2.9.2",
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (v+"-0.2.11.1")),//(v + "-0.2.11.1")),
    addSbtPlugin("com.typesafe" % "sbt-native-packager" % "0.4.4"),
    addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.1.0")
  )
  lazy val root = Project("build", file("."))
}