package com.mle.wicket.component

import org.apache.wicket.Component

/**
 *
 * @author mle
 */
trait EnabledToggle extends Component {
  def enabled: Boolean

  override def onConfigure() {
    super.onConfigure()
    setEnabled(enabled)
  }
}
