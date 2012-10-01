package com.mle.wicket.behavior

import org.apache.wicket.Component
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior
import org.apache.wicket.ajax.AjaxRequestTarget

/**
 * @author Mle
 */
trait SOnChangeAjaxBehavior extends Component {
  val onChangeBehavior = new OnChangeAjaxBehavior {
    def onUpdate(target: AjaxRequestTarget) {
      onUpdate(target)
    }
  }
  add(onChangeBehavior)

  def onUpdate: AjaxRequestTarget => Unit
}
