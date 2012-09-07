package com.mle.web.component

import java.util.{List => JList}
import org.apache.wicket.model.IModel
import org.apache.wicket.markup.html.list.{ListView, ListItem}

/**
 * @author Mle
 */
class SListView[T](id: String, model: IModel[_ <: JList[_ <: T]])(layout: ListItem[T] => Unit)
  extends ListView[T](id, model) {
  override def populateItem(item: ListItem[T]) {
    layout(item)
  }
}