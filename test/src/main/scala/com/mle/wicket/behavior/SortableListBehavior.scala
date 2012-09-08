package com.mle.wicket.behavior

import org.odlabs.wiquery.ui.sortable.SortableBehavior
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.Component
import org.apache.wicket.markup.html.list.ListItem
import com.mle.util.Log
import collection.mutable
import org.apache.wicket.model.IModel
import java.util.{List => JList}
import collection.JavaConversions._

/**
 * Sortable jQuery behavior for [[org.apache.wicket.markup.html.list.ListView]]s that updates the backing model of the list as sorting takes place.
 * <br>
 * Astonishingly, WiQuery doesn't do this automatically: http://code.google.com/p/wiquery/issues/detail?id=202
 * <br><br>
 * For this to work, the user must setOutputMarkupId(true) to the [[org.apache.wicket.markup.html.list.ListView]]'s [[org.apache.wicket.markup.html.list.ListItem]]s during listview creation.
 *
 * @author Mle
 */

class SortableListBehavior[T](model: IModel[_ <: JList[_ <: T]]) extends SortableBehavior with Log {
  val updateCallback = new SortableBehavior.AjaxUpdateCallback {
    def update(target: AjaxRequestTarget, source: Component, sortIndex: Int, sortItem: Component) {
      val srcIndex = sortItem.asInstanceOf[ListItem[T]].getIndex
      val destIndex = sortIndex - 1
      swap(model.getObject, srcIndex, destIndex)
      log debug "Moved list item from index: " + srcIndex + " to index: " + destIndex
      // source is the parent container of the ListView
      target add source
    }
  }
  setUpdateEvent(updateCallback)

  /**
   * Changes the position of an element in a buffer.
   * @param buf the buffer to modify
   * @param before the index of the element to move
   * @param after the new position of the element
   * @tparam Y type of element
   */
  def swap[Y](buf: mutable.Buffer[Y], before: Int, after: Int) {
    if (before != after)
      buf.insert(after, buf.remove(before))
  }
}