package com.mle.util.security

import com.mle.exception.ResourceNotFoundException
import com.mle.util.Util
import java.nio.file.{Paths, Path}

/**
 *
 * @author mle
 *
 * @tparam T type of credential
 */
trait BaseCredentialReader[T] extends CredentialReader[T] {
  protected val userHome = Paths get sys.props("user.home")

  /**
   * Attempts to read credentials from the following locations, in this order:
   *
   * environment variables
   * a resource file packaged with the app, or else a file at the same path
   * a file under the user home dir
   *
   * @return credentials
   * @throws ResourceNotFoundException if no credentials are found in any of the searched locations
   */
  def load = loadOpt
    .getOrElse(throw new ResourceNotFoundException("Unable to load credentials from environment, resource or user home path."))

  def loadOpt = fromEnvOpt orElse fromResourceOpt(resourceCredential) orElse fromUserHomeOpt

  def fromEnvOpt = fromMapOpt(sys.env)

  def fromResourceOpt(res: String) = Util propsOpt res flatMap fromMapOpt

  def fromUserHomeOpt = fromPathOpt(userHomeCredential)

  def fromPathOpt(path: Path) = fromMapOpt(Util props path.toAbsolutePath.toString)

  def fromEnv = fromEnvOpt
    .getOrElse(throw new IllegalArgumentException("Unable to read credentials from environment variables"))

  def fromResource(res: String) = fromResourceOpt(res)
    .getOrElse(throw new IllegalArgumentException(s"Missing parameters in: $res"))

  def fromUserHome = fromPath(userHomeCredential)

  def fromPath(path: Path) = fromPathOpt(path)
    .getOrElse(throw new IllegalArgumentException(s"Unable to read credentials from path: ${path.toAbsolutePath}"))
}