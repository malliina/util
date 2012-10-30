package com.mle.wicket.component

import org.apache.wicket.model.IModel
import com.mle.wicket.model.LDModel
import org.apache.wicket.markup.html.basic.Label

/**
 *
 * @author mle
 */
class SLabel[T](id: String, model: IModel[T]) extends Label(id, model) {
  def this(id: String, value: => T) = this(id, LDModel(value))
}

object SLabel {
  /**
   * Creates a label with a loadable detachable model wrapping its value.
   *
   * @param id
   * @param value
   * @tparam T
   * @return
   */
  def apply[T](id: String, value: => T) = new SLabel[T](id, value)
}