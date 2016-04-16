import sbt.Keys._
import sbt._

/** Build definition of the build definition. I don't like build.sbt/plugins.sbt files.
  *
  * This replacement gives better IDE support and a more consistent approach to configuration.
  */
object BuildBuild extends Build {

  // "build.sbt" goes here
  override lazy val settings = super.settings ++ Seq(
    scalaVersion := "2.10.6",
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    resolvers ++= Seq(
      "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      ivyResolver("bintray-sbt-plugin-releases", url("https://dl.bintray.com/content/sbt/sbt-plugin-releases")),
      ivyResolver("malliina bintray sbt", url("https://dl.bintray.com/malliina/sbt-plugins/"))
    )
  ) ++ plugins

  def ivyResolver(name: String, repoUrl: sbt.URL) =
    Resolver.url(name, repoUrl)(Resolver.ivyStylePatterns)

  def plugins = Seq(
    "com.malliina" % "sbt-utils" % "0.3.0",
    "me.lessis" % "bintray-sbt" % "0.3.0"
  ) map addSbtPlugin

  lazy val root = Project("plugins", file("."))
}
