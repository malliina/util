package com.malliina.util

import java.nio.file.Path

/** Reads configuration data from various local sources.
  *
  * Users of this class mainly use `load` to load the config when needed. The rest of the methods can be overridden for
  * customization.
  *
  * @tparam T type of config
  */
trait ConfigReader[T] {
  /** Attempts to read the config.
    *
    * @return the config
    * @throws com.malliina.exception.ResourceNotFoundException if no valid config is found in any of the searched locations
    */
  def load: T

  /** Attempts to read the config. Override this method to customize a) the locations from which the
    * config is read and b) the order in which the locations are read.
    *
    * @return the config wrapped in an `Option` if successfully read, `None` otherwise
    */
  def loadOpt: Option[T]

  def filePath: Option[Path]

  def fromMapOpt(map: Map[String, String]): Option[T]

  def fromEnvOpt: Option[T]

  def fromResourceOpt(res: String): Option[T]

  def fromPathOpt(path: Path): Option[T]

  def fromEnv: T

  def fromResource(res: String): T

  def fromUserHome: T

  def fromPath(path: Path): T
}
