package com.mle.wicket.behavior

import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior

/**
 * @author Mle
 */

class OnChangeAjaxFormBehavior(onChange: AjaxRequestTarget => Unit = target => ())
  extends AjaxFormComponentUpdatingBehavior("onchange") {
  def onUpdate(target: AjaxRequestTarget) {
    onChange(target)
  }
}