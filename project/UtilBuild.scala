import sbt.Keys._
import sbt._
import Dependencies._

/**
 * @author Mle
 */

object UtilBuild extends Build {
  val releaseVersion = "0.7.2"
  val snapshotVersion = "0.7.2-SNAPSHOT"

  lazy val util = testableProject("util", deps = Seq(commonsIO, commonsCodec) ++ loggingDeps)
    .settings(version := snapshotVersion)
  lazy val actor = utilProject("util-actor", deps = Seq(akkaActor, akkaTestKit))
    .settings(version := snapshotVersion)
  lazy val rmi = utilProject("util-rmi")
    .settings(version := snapshotVersion)
  lazy val jdbc = utilProject("util-jdbc", deps = Seq(tomcatJdbc, boneCp, mysql))
    // Kids, watch and learn. auth % "test->test" means this module's tests depend on tests in module auth
    .dependsOn(auth % "compile->compile;test->test")
  lazy val utilWeb = utilProject("util-web", deps = webDeps)
  lazy val auth = utilProject("util-auth", deps = Seq(commonsCodec))
  lazy val utilAzure = testableProject("util-azure", deps = Seq(azureApi, utilDep))
    .settings(version := releaseVersion)

  // Hack for play compat
  //  override def settings = super.settings ++ com.typesafe.sbtidea.SbtIdeaPlugin.ideaSettings

  val commonSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.github.malliina",
    version := snapshotVersion,
    scalaVersion := "2.10.0",
    retrieveManaged := false,
    resolvers += "Sonatype snaps" at "http://oss.sonatype.org/content/repositories/snapshots/",
    publishTo <<= (version)(v => {
      val repo =
        if (v endsWith "SNAPSHOT") {
          "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
        } else {
          "Sonatype releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
        }
      Some(repo)
    }),
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

  def testableProject(id: String, deps: Seq[ModuleID] = Seq.empty) =
    Project(id, file(id), settings = commonSettings).settings(
      libraryDependencies ++= deps ++ Seq(scalaTest)
    )

  def utilProject(id: String, deps: Seq[ModuleID] = Seq.empty) =
    testableProject(id, deps)
      .dependsOn(util)
}