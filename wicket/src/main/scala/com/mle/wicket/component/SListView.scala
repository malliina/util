package com.mle.wicket.component

import java.util.{List => JList}
import org.apache.wicket.markup.html.list.{ListView, ListItem}
import org.apache.wicket.model.IModel

/**
 * @author Mle
 */
class SListView[T](id: String, model: IModel[_ <: JList[_ <: T]])(layout: ListItem[T] => Unit)
  extends ListView[T](id, model) {

  override def populateItem(item: ListItem[T]) {
    layout(item)
  }
}