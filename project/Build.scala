import sbt._

package

/**
 * @author Mle
 */

object GitBuild extends Build {
  lazy val util = Project("common-util", file("common-util"))
  lazy val test = Project("test", file("test")).dependsOn(util)
}