package com.mle.wicket.model

import org.apache.wicket.model.AbstractReadOnlyModel

/**
 * @author Mle
 */

class ReadOnlyModel[T](getter: => T) extends AbstractReadOnlyModel[T] {
  def getObject = getter
}

object ReadOnlyModel {
  def apply[T](getter: => T) = new ReadOnlyModel(getter)
}
