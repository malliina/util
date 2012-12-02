package com.mle.web.wicket.model

import org.apache.wicket.model.IModel

/**
 * @author Mle
 */

class RWModel[T](getter: => T, setter: T => Unit) extends IModel[T] {
  def getObject = getter

  def setObject(obj: T) {
    setter(obj)
  }

  def detach() {}
}

object RWModel {
  def apply[T](getter: => T, setter: T => Unit) = new RWModel(getter, setter)
}