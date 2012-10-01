package com.mle.wicket.component

import org.apache.wicket.markup.html.form.DropDownChoice
import org.apache.wicket.model.IModel
import collection.JavaConversions._
import com.mle.wicket.behavior.SOnChangeAjaxBehavior

/**
 * @author Mle
 */
abstract class AjaxDropDown[T](id: String, model: IModel[T], choices: Seq[T])
  extends DropDownChoice[T](id, model, choices) with SOnChangeAjaxBehavior {
}
