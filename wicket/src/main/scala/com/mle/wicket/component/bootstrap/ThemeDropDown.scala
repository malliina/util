package com.mle.wicket.component.bootstrap

import org.apache.wicket.markup.html.form.DropDownChoice
import com.mle.wicket.model.RWModel
import com.mle.wicket.Bootstrapping
import collection.JavaConversions._

/**
 * @author Mle
 */
class ThemeDropDown(id: String) extends DropDownChoice[String](id) {
  setModel(RWModel(themeService.active, themeService.activate(_)))
  setChoices(themeService.themeNames)

  override val wantOnSelectionChangedNotifications = true

  def themeService = getApplication.asInstanceOf[Bootstrapping].themeService
}
