package com.mle.wicket.markup

import collection.JavaConversions._
import com.mle.util.Log
import com.mle.wicket.behavior.SortableListBehavior
import com.mle.wicket.component.{AjaxMarkup, SListView}
import java.util.{ArrayList => JArrayList}
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.panel.Panel
import com.mle.web.wicket.model.LDModel

/**
 * @author Mle
 */

class SortPanel(id: String) extends Panel(id) with Log {
  val sortMarkup = new AjaxMarkup("wmc")
  add(sortMarkup)
  val initialSort = new JArrayList(Seq("a", "b", "c", "d", "e"))
  val sortModel = LDModel[JArrayList[String]](initialSort)
  val sortableList = new SListView("list", sortModel)(item => {
    item setOutputMarkupId true
    item add new Label("item", item.getModel)
  })
  sortMarkup add sortableList
  sortMarkup add new SortableListBehavior[String](sortModel)
}