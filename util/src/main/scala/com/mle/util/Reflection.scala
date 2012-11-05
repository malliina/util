package com.mle.util

/**
 * Hacks.
 *
 * @author mle
 */
object Reflection {
  def declaredFields(obj: AnyRef) = obj.getClass.getDeclaredFields

  def declaredClasses(obj: AnyRef) = obj.getClass.getDeclaredClasses

  def names(obj: AnyRef) = {
    declaredFields(obj).map(_.getName).toSeq
  }

  def name(obj: AnyRef) = obj.getClass.getSimpleName.takeWhile(_ != '$')

  def fieldName(parent: AnyRef, obj: AnyRef) = {
    val fields = declaredFields(parent)
    fields.foreach(_.setAccessible(true))
    fields.find(_.get(parent) == obj)
      .map(_.getName)
      .getOrElse(throw new Exception("Unable to find: " + obj + " in: " + parent))
  }

  def objectName(parent: AnyRef, obj: AnyRef) = {
    val objs = declaredClasses(parent)
    //    objs.find(_ == )
  }

  def objects(parent: AnyRef) = {
    val (scalaObjects, javaClasses) = declaredClasses(parent)
      .partition(_.getName.contains('$'))
    val javaNames = javaClasses.map(_.getSimpleName)
    val scalaNames = scalaObjects.map(_.getName.reverse.tail.takeWhile(_ != '$').reverse)
    (javaNames ++ scalaNames).toSeq
  }

  def className(obj: AnyRef) = {
    val clazz = obj.getClass
    val longName = clazz.getName
    // for a scala class named Test, longName is: com.mle.jdbc.tests.Test$
    if (longName.contains('$')) {
      // getSimpleName throws an exception if it sees a '$' so we parse it by hand
      longName.reverse.tail.takeWhile(c => c != '$' && c != '.').reverse
    } else {
      clazz.getSimpleName
    }
  }
}