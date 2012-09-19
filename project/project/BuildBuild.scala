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
    sbtVersion := "0.12",
    scalaVersion := "2.9.2",
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/", // for play plugin
    //    resolvers += Resolver.url("scalasbt", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns), // only needed if sbt version < 0.12
    libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % ("0.12.0-0.2.11.1")), //(v + "-0.2.11.1")),
    addSbtPlugin("com.typesafe" % "sbt-native-packager" % "0.4.4"),
    addSbtPlugin("play" % "sbt-plugin" % "2.1-09092012") //"2.1-09092012"),
  )
  lazy val root = Project("build", file("."))
    .aggregate(packagerPlugin)
    .dependsOn(packagerPlugin)
  lazy val packagerPlugin = uri("git://github.com/malliina/sbt-packager")
}