package controllers.util

/**
 *
 * @author Mle
 */
trait PlayLog {
  // Only works with "application" although logback.xml is correctly configured, hmm?
  val log = play.Logger.of("application")
}
