package com.mle.wicket.markup

import ch.qos.logback.classic.Level
import collection.JavaConversions._
import com.mle.util.AppUtils
import com.mle.wicket.behavior.OnChangeAjaxFormBehavior
import com.mle.wicket.model.RWModel
import org.apache.wicket.markup.html.form.DropDownChoice
import org.apache.wicket.markup.html.panel.Panel

/**
 * @author Mle
 */

class Settings(id: String) extends Panel(id) {
  val levels = Seq(Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.OFF)
  val levelDropDown = new DropDownChoice("logLevel", RWModel[Level](AppUtils.getLogLevel, AppUtils.setLogLevel(_)), levels)
  levelDropDown add new OnChangeAjaxFormBehavior()
  add(levelDropDown)
}