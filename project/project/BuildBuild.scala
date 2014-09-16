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
    scalaVersion := "2.10.4",
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    resolvers ++= Seq(
      "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/")
  ) ++ plugins

  def plugins = Seq(
    "com.github.malliina" % "sbt-utils" % "0.0.5",
    "com.timushev.sbt" % "sbt-updates" % "0.1.2"
  ) map addSbtPlugin

  lazy val root = Project("plugins", file("."))
}