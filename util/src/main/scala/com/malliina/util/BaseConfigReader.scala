package com.malliina.util

import java.io.FileNotFoundException
import java.nio.file.Path

import com.malliina.exception.ResourceNotFoundException
import com.malliina.file.FileUtilities

/**
  * @tparam T type of credential
  */
trait BaseConfigReader[T] extends ConfigReader[T] {
  protected val userHome = FileUtilities.userHome

  /** Attempts to read the config from the following locations, in this order:
    *
    * environment variables
    * a resource file packaged with the app, or else a file at the same path
    * a file under the user home dir
    *
    * @return credentials
    * @throws ResourceNotFoundException if no config is found in any of the searched locations
    */
  def load = loadOpt
    .getOrElse(throw new ResourceNotFoundException("Unable to load config from environment, resource or user home path."))

  def loadOpt = fromEnvOpt orElse fromUserHomeOpt

  def fromEnvOpt = fromMapOpt(sys.env)

  def fromSysPropsOpt = fromMapOpt(sys.props.toMap)

  def fromResourceOpt(res: String) = Util propsOpt res flatMap fromMapOpt

  def fromUserHomeOpt: Option[T] = filePath.flatMap(fromPathOpt)

  def fromPathOpt(path: Path): Option[T] = fromMapOpt(Util props path)

  def fromEnv = fromEnvOpt
    .getOrElse(throw new IllegalArgumentException("Unable to read credentials from environment variables"))

  def fromResource(res: String) = fromResourceOpt(res)
    .getOrElse(throw new IllegalArgumentException(s"Missing parameters in: $res"))

  def fromUserHome = filePath.map(fromPath).getOrElse(throw new FileNotFoundException)

  def fromPath(path: Path) = fromPathOpt(path)
    .getOrElse(throw new IllegalArgumentException(s"Unable to read credentials from path: ${path.toAbsolutePath}"))
}
