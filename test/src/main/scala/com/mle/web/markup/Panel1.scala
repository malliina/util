package com.mle.web.markup

import org.apache.wicket.markup.html.panel.Panel
import com.mle.util.Log
import com.mle.web.component.SAjaxLink

/**
 * @author Mle
 */

class Panel1(id: String) extends Panel(id) with Log {
  val link = SAjaxLink("link")(target => log info "Pressed link; target: " + target)
  add(link)
}