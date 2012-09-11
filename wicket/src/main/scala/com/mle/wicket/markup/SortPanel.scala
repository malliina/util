package com.mle.wicket.markup

import collection.JavaConversions._
import com.mle.util.Log
import com.mle.wicket.behavior.SortableListBehavior
import com.mle.wicket.component.{AjaxMarkup, SListView}
import com.mle.wicket.model.LDModel
import java.util.{ArrayList => JArrayList}
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.panel.Panel

/**
 * @author Mle
 */

class SortPanel(id: String) extends Panel(id) with Log {
  val sortMarkup = new AjaxMarkup("wmc")
  add(sortMarkup)
  val sortModel2 = LDModel[JArrayList[String]](new JArrayList(Seq("a", "b", "c", "d", "e")))
  val sortableList = new SListView("list2", sortModel2)(item => {
    item setOutputMarkupId true
    item add new Label("item2", item.getModel)
  })
  sortMarkup add sortableList
  sortMarkup add new SortableListBehavior[String](sortModel2)
}