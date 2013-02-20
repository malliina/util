import sbt.Keys._
import sbt._
import Dependencies._

/**
 * @author Mle
 */

object GitBuild extends Build {
  val commonSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.github.malliina",
    version := "0.69-SNAPSHOT",
    scalaVersion := "2.10.0",
    retrieveManaged := false,
    resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
    publishTo := Some("sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"),
    credentials += Credentials(Path.userHome / ".ivy2" / "sonatype.txt"),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := (_ => false),
    pomExtra := extraPom,
    // system properties seem to have no effect in tests,
    // causing e.g. tests requiring javax.net.ssl.keyStore props to fail
    // ... unless fork is true
    sbt.Keys.fork in Test := true,
    // the jars of modules depended on are not included unless this is true
    exportJars := true
  )

  def extraPom = (
    <url>https://github.com/malliina/util</url>
      <licenses>
        <license>
          <name>BSD-style</name>
          <url>http://www.opensource.org/licenses/BSD-3-Clause</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:malliina/util.git</url>
        <connection>scm:git:git@github.com:malliina/util.git</connection>
      </scm>
      <developers>
        <developer>
          <id>malliina</id>
          <name>Michael Skogberg</name>
          <url>http://mskogberg.info</url>
        </developer>
      </developers>)


  lazy val parent = Project("parent", file("."), settings = commonSettings)
    .aggregate(util, actor, jdbc, utilWeb, rmi, auth)
  // last 2.9.2 is 0.63-SNAPSHOT
  // 0.63-SNAPSHOT is an sbt plugin
  lazy val util = myProject("util")
    .settings(
//        scalaVersion := "2.9.2",
    //    sbtPlugin := true,
    libraryDependencies ++= loggingDeps ++ Seq(commonsIO, scalaTest)
  )
  lazy val actor = basicProject("util-actor")
    .settings(libraryDependencies ++= Seq(akkaActor, akkaTestKit))
  lazy val jdbc = basicProject("util-jdbc")
    // Kids, watch and learn. auth % "test->test" means this module's tests depend on tests in module auth
    .dependsOn(auth % "compile->compile;test->test")
    .settings(libraryDependencies ++= Seq(tomcatJdbc, boneCp, mysql))
  lazy val utilWeb = basicProject("util-web")
    .settings(libraryDependencies ++= webDeps)
  lazy val rmi = basicProject("util-rmi")
  lazy val auth = basicProject("util-auth")
    .settings(libraryDependencies ++= Seq(hashing))

  def myProject(id: String, customSettings: Seq[Project.Setting[_]] = Seq.empty) = Project(id, file(id), settings = commonSettings ++ customSettings)

  def basicProject(id: String, customSettings: Seq[Project.Setting[_]] = Seq.empty) = myProject(id, customSettings)
    .dependsOn(util)
    .settings(libraryDependencies += scalaTest)
}