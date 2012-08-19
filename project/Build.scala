import org.clapper.sbt.izpack.IzPack.IzPack
import sbt._
import sbt.Keys._

package

/**
 * @author Mle
 */

object GitBuild extends Build {
  lazy val util = Project("common-util", file("common-util"), settings = mySettings)
  lazy val test = Project("test", file("test"), settings = mySettings).dependsOn(util)

  def mySettings = commonSettings ++ IzPack.settings

  val commonSettings = Defaults.defaultSettings ++ Seq(
    scalaVersion := "2.9.2",
    version := "0.1-SNAPSHOT",
    //      resolvers := additionalRepos,
    exportJars := true,
    retrieveManaged := true
  )
  IzPack.variables in IzPack.Config <+= name {
    name => ("projectName", "My test project")
  }
  IzPack.variables in IzPack.Config +=("author", "Michael Skogberg")
}