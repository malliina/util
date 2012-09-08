package com.mle.wicket.component

import org.apache.wicket.ajax.markup.html.AjaxFallbackLink
import org.apache.wicket.ajax.AjaxRequestTarget

/**
 * @author Mle
 */

class SAjaxLink(id: String)(onClicked: AjaxRequestTarget => Unit) extends AjaxFallbackLink(id) {
  def onClick(target: AjaxRequestTarget) {
    onClicked(target)
  }
}

object SAjaxLink {
  def apply(id: String)(onClicked: AjaxRequestTarget => Unit) = new SAjaxLink(id)(onClicked)
}