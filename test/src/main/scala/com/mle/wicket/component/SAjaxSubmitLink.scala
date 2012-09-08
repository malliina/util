package com.mle.wicket.component

import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink
import org.apache.wicket.markup.html.form.Form

/**
 * @author Mle
 */

class SAjaxSubmitLink(id: String)(submitAction: AjaxRequestTarget => Unit)
  extends AjaxSubmitLink(id) {
  override def onSubmit(target: AjaxRequestTarget, form: Form[_]) {
    submitAction(target)
  }
}