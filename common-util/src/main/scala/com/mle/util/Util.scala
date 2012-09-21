package com.mle.util

import java.io.{Closeable, FileWriter, BufferedWriter, PrintWriter}
import java.net.URL
import com.mle.exception.ResourceNotFoundException

/**
 * Utility methods.
 * @author Mle
 */
object Util {
  /**
   * @see <a href="http://stackoverflow.com/a/4608061">http://stackoverflow.com/a/4608061</a>
   * @param filename the file to write to
   * @param op the file writing code
   */
  def writerTo(filename: String)(op: PrintWriter => Unit) {
    using(new PrintWriter(new BufferedWriter(new FileWriter(filename))))(op)
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
   * @see [[com.mle.util.Util]]#resource
   */
  def using[T <: Closeable, U](resource: T)(op: T => U): U =
    try {
      op(resource)
    } finally {
      resource.close()
    }

  /**
   * Attempts to compute <code>attempt</code>, suppressing any exceptions
   * @param attempt
   * @return attempt wrapped in an [[scala.Option]], or [[scala.None]] if any exception is thrown
   */
  def optionally[T](attempt: => T): Option[T] =
    try {
      Some(attempt)
    } catch {
      case e: Exception => None
    }

  def addShutdownHook(code: => Unit) {
    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run() {
        code
      }
    })
  }

  def resource(path: String): URL = Option(getClass.getClassLoader.getResource(path))
    .getOrElse(throw new ResourceNotFoundException("Unable to locate resource: " + path))
}