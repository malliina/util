package com.mle.wicket.model

import org.apache.wicket.model.LoadableDetachableModel

/**
 * @author Mle
 */

class LDModel[T](getter: => T) extends LoadableDetachableModel[T] {
  override def load() = getter
}

object LDModel {
  def apply[T](getter: => T) = new LDModel(getter)
}