import sbt._
package

/**
 * @author Mle
 */

object GitBuild extends Build {
  lazy val test = Project("test", file("test"))
}