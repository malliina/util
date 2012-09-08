package com.mle.wicket.markup

import org.apache.wicket.markup.html.panel.Panel
import java.util.{ArrayList => JArrayList}
import org.apache.wicket.model.Model
import collection.JavaConversions._
import org.apache.wicket.markup.html.basic.Label
import com.mle.wicket.component.SListView
import org.apache.wicket.markup.html.form.Form
import com.mle.util.Log
import com.mle.wicket.behavior.SortableListBehavior

/**
 * @author Mle
 */

class SortPanel(id: String) extends Panel(id) with Log {
  val items = new JArrayList(Seq("aaa", "bbb", "ccc", "ddd"))
  val form = new Form("form") {
    override def onSubmit() {
      log info list.getModelObject.mkString(", ")
    }
  }
  add(form)
  val list = new SListView("list", Model.of[JArrayList[String]](items))(item => {
    item add new Label("item", item.getModel)
    // for sortable to work
    item setOutputMarkupId true
  })
  form add list
  val sortable = new SortableListBehavior[String](list.getModel)
  form add sortable
}