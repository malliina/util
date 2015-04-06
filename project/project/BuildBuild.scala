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
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      Resolver.url(
        "bintray-sbt-plugin-releases",
        url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
          Resolver.ivyStylePatterns))
  ) ++ plugins

  def plugins = Seq(
    "com.github.malliina" % "sbt-utils" % "0.1.0",
    "me.lessis" % "bintray-sbt" % "0.2.1"
  ) map addSbtPlugin

  lazy val root = Project("plugins", file("."))
}
