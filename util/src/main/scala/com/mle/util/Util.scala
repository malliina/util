package com.mle.util

import java.net.URL
import com.mle.exception.ResourceNotFoundException
import java.nio.file.{Path, Files}
import reflect.Manifest
import scala.io.BufferedSource

/**
 * Utility methods.
 * @author Mle
 */
object Util {
  /**
   * Turns on SSL debug logging.
   *
   * User for SSL troubleshooting.
   */
  def sslDebug() {
    sys.props("javax.net.debug") = "ssl"
  }

  /**
   * Performs the given operation on the provided closeable resource after which the resource is closed.
   * @see [[com.mle.util.Util]].using
   * @param resource the resource to operate on: a file reader, database connection, ...
   * @param op the operation to perform on the resource: read/write to a file, database, ...
   * @tparam T closeable resource
   * @tparam U result of the operation
   * @return the result of the operation
   */
  def resource[T <: {def close()}, U](resource: T)(op: T => U): U =
    try {
      op(resource)
    } finally {
      resource.close()
    }

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

  /**
   * Attempts to compute <code>attempt</code>, suppressing the specified exception.
   * todo: consider using either
   *
   * @param attempt
   * @return attempt wrapped in an [[scala.Option]], or [[scala.None]] if an exception of type U is thrown
   */
  def optionally[T, U <: Throwable](attempt: => T)(implicit manifest: Manifest[U]): Option[T] =
    try {
      Some(attempt)
    } catch {
      case e: U => None
    }

  def addShutdownHook(code: => Unit) {
    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run() {
        code
      }
    })
  }

  def resource(resource: String): URL = obtainResource(resource, l => l.getResource _)

  def resourceUri(path: String) = resource(path).toURI

  def openStream(resource: String) = obtainResource(resource, l => l.getResourceAsStream _)

  def obtainResource[T](resource: String, getter: ClassLoader => String => T): T =
    Option(getter(getClass.getClassLoader)(resource))
      .getOrElse(throw new ResourceNotFoundException("Unable to locate resource: " + resource))

  /**
   *
   * @param path
   * @return the uri of the file at the given path, or classpath resource if no classpath resource is found
   * @throws ResourceNotFoundException if neither a resource nor a file is found
   */
  def uri(path: String) = {
    val maybeFile = FileUtilities.pathTo(path)
    if (Files exists maybeFile)
      maybeFile.toUri
    else
      resourceUri(path)
  }

  def url(path: String) = uri(path).toURL

  /**
   * Reads the properties of the classpath resource at the given path, or if none is found, of a file at the given path.
   *
   * @param path
   * @return the properties as a map
   * @throws ResourceNotFoundException if neither a resource nor a file is found
   */
  def props(path: String) = mappify(io.Source.fromURL(url(path)))

  def props(path: Path) = mappify(io.Source.fromFile(path.toUri))

  private def mappify(src: BufferedSource) = src.getLines()
    .map(line => line.split("=", 2))
    .map(arr => arr(0) -> arr(1)).toMap
}