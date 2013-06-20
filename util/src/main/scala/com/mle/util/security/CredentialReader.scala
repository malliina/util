package com.mle.util.security

import java.nio.file.Path

/**
 *
 * @tparam T type of credential
 */
trait CredentialReader[T] {
  /**
   * Attempts to read the credentials.
   *
   * @return the credentials
   * @throws com.mle.exception.ResourceNotFoundException if no credentials are found in any of the searched locations
   */
  def load: T

  def loadOpt: Option[T]

  def userHomeCredential: Path

  def resourceCredential: String

  def fromMapOpt(map: Map[String, String]): Option[T]

  def fromEnvOpt: Option[T]

  def fromResourceOpt(res: String): Option[T]

  def fromPathOpt(path: Path): Option[T]

  def fromEnv: T

  def fromResource(res: String): T

  def fromUserHome: T

  def fromPath(path: Path): T
}
