package com.mle.wicket.component.bootstrap

import de.agilecoders.wicket.markup.html.bootstrap.tabs.BootstrapTabbedPanel
import org.apache.wicket.extensions.markup.html.tabs.ITab
import collection.JavaConversions._
import org.apache.wicket.behavior.AttributeAppender
import org.apache.wicket.model.Model

/**
 * @author Mle
 */
class LRBootstrapTabbedPanel[T <: ITab](id: String, leftTabs: Seq[T], rightTabs: Seq[T] = Seq.empty)
  extends BootstrapTabbedPanel(id, leftTabs ++ rightTabs.reverse) {
  private val splitIndex = leftTabs.size

  override def newTabContainer(tabIndex: Int) = {
    val tabContainer = super.newTabContainer(tabIndex)
    if (tabIndex >= splitIndex)
      tabContainer add new AttributeAppender("class", Model.of("pull-right"), " ")
    tabContainer
  }
}