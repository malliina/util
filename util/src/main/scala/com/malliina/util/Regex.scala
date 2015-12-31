package com.malliina.util

import java.util.regex.Pattern

/**
 *
 * @author mle
 */
object Regex extends Log {
  /**
   * Matches the input against the regex. If the regex matches the input and contains one or more groups,
   * the first group is returned. If there's a match but no group is specified, the match from the entire pattern is returned.
   *
   * @param input input to match
   * @param regex regex to use
   * @return the matched input as described above, or None if the regex did not match the input
   */
  def parse(input: String, regex: String): Option[String] = {
    val pattern = Pattern compile regex
    val matcher = pattern matcher input
    if (matcher.find()) {
      log debug "Regex: " + regex + " matches: " + input
      // if no group is specified we default to "group 0" which is the whole input
      val groupIndex = if (matcher.groupCount() > 0) 1 else 0
      Some(matcher group groupIndex)
    } else {
      log debug "Regex: " + regex + " does not match: " + input
      None
    }
  }
}
