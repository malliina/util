package com.mle.util

import java.net.URL
import com.mle.exception.ResourceNotFoundException
import java.nio.file.{Path, Files}
import reflect.Manifest
import scala.io.BufferedSource

/**
 * Utility methods.
 *
 * @author Mle
 */
object Util {
  /**
   * try-with-resources Scala style
   *
   * @see [[com.mle.util.Util]]#resource
   */
  def using[T <: AutoCloseable, U](resource: T)(op: T => U): U =
    try {
      op(resource)
    } finally {
      resource.close()
    }

  def addShutdownHook(code: => Unit) {
    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run() {
        code
      }
    })
  }

  def resource(resource: String): URL = obtainResource(resource, _.getResource)

  def resourceOpt(resource: String) = obtainResourceOpt(resource, _.getResource)

  def resourceUri(path: String) = resource(path).toURI

  def resourceUriOpt(path: String) = resourceOpt(path) map (_.toURI)

  def openStream(resource: String) = obtainResource(resource, _.getResourceAsStream)

  def obtainResource[T](resource: String, getter: ClassLoader => String => T): T =
    obtainResourceOpt(resource, getter)
      .getOrElse(throw new ResourceNotFoundException("Unable to locate resource: " + resource))

  def obtainResourceOpt[T](resource: String, getter: ClassLoader => String => T): Option[T] =
    Option(getter(getClass.getClassLoader)(resource))

  /**
   *
   * @return the uri of the file at the given path, or classpath resource if no classpath resource is found
   * @throws ResourceNotFoundException if neither a resource nor a file is found
   */
  def uri(path: String) =
    fileUriOpt(path) getOrElse resourceUri(path)

  def uriOpt(path: String) =
    fileUriOpt(path) orElse resourceUriOpt(path)

  def url(path: String) = uri(path).toURL

  def fileUriOpt(path: String) = {
    val maybeFile = FileUtilities pathTo path
    if (Files exists maybeFile) {
      Some(maybeFile.toUri)
    } else {
      None
    }
  }

  /**
   * Reads the properties of the classpath resource at the given path, or if none is found, of a file at the given path.
   *
   * @return the properties as a map
   * @throws ResourceNotFoundException if neither a resource nor a file is found
   */
  def props(path: String) = Utils.resource(io.Source.fromURL(url(path)))(mappify)

  def props(path: Path) = Utils.resource(io.Source.fromFile(path.toUri))(mappify)

  def propsOpt(path: String) = {
    uriOpt(path).map(r => Utils.resource(io.Source.fromURI(r))(mappify))
  }

  private def mappify(src: BufferedSource) = src.getLines()
    .filter(line => line.contains("=") && !line.startsWith("#") && !line.startsWith("//"))
    .map(line => line.split("=", 2))
    .filter(_.size >= 2)
    .map(arr => arr(0) -> arr(1)).toMap

  /**
   * Turns on SSL debug logging.
   *
   * Use for SSL troubleshooting.
   */
  def sslDebug() {
    sys.props("javax.net.debug") = "ssl"
  }
}