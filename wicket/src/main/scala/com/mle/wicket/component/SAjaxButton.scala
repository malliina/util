package com.mle.wicket.component

import org.apache.wicket.model.IModel
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.form.AjaxButton
import org.apache.wicket.markup.html.form.Form

/**
 *
 * @author mle
 */
class SAjaxButton(id: String, title: IModel[String])(onClick: AjaxRequestTarget => Unit)
  extends AjaxButton(id, title) {
  override def onSubmit(target: AjaxRequestTarget, form: Form[_]) {
    onClick(target)
  }
}
