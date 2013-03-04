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
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
    resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/", // for play plugin
    addSbtPlugin("com.typesafe.sbt" % "sbt-start-script" % "0.6.0"),
    addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.3.0-SNAPSHOT"),
    addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.8")
  )
  lazy val root = Project("plugins", file("."))
}