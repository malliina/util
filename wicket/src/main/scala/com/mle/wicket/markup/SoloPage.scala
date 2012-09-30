package com.mle.wicket.markup

import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.markup.head.IHeaderResponse
import de.agilecoders.wicket.Bootstrap

/**
 * @author Mle
 */
class SoloPage extends WebPage {
  override def renderHead(response: IHeaderResponse) {
    super.renderHead(response)
    Bootstrap.renderHead(response)
  }
}
