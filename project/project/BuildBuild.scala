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
    resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/", // for play plugin
    libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % ("0.12.0-0.2.11.1")),
    addSbtPlugin("play" % "sbt-plugin" % "2.1-SNAPSHOT"),
    addSbtPlugin("eu.getintheloop" %% "sbt-cloudbees-plugin" % "0.4.1"),
    addSbtPlugin("com.mle" % "sbt-packager" % "0.6-SNAPSHOT"),
    addSbtPlugin("com.typesafe.sbt" % "sbt-start-script" % "0.6.0")
  )
  lazy val root = Project("plugins", file("."))
  //    .dependsOn(nativePackager)
  //  lazy val nativePackager = uri("git://github.com/Dremora/sbt-native-packager.git")

}