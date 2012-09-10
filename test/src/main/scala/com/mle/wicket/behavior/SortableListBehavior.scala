package com.mle.wicket.behavior

import com.mle.util.Log
import java.util.{List => JList, ArrayList => JArrayList}
import org.apache.wicket.Component
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.model.IModel
import org.odlabs.wiquery.ui.sortable.SortableBehavior

/**
 * Sortable jQuery behavior for [[org.apache.wicket.markup.html.list.ListView]]s that updates the backing model of the list as sorting takes place.
 * <br>
 * Astonishingly, WiQuery doesn't do this automatically: http://code.google.com/p/wiquery/issues/detail?id=202
 * <br><br>
 * For this to work, the user must setOutputMarkupId(true) to the [[org.apache.wicket.markup.html.list.ListView]]'s [[org.apache.wicket.markup.html.list.ListItem]]s during listview creation.
 *
 * @author Mle
 */

class SortableListBehavior[T](model: IModel[JArrayList[T]]) extends SortableBehavior with Log {
  val updateCallback = new SortableBehavior.AjaxUpdateCallback {
    def update(target: AjaxRequestTarget, source: Component, sortIndex: Int, sortItem: Component) {
      if (sortItem != null) {
        val srcIndex = sortItem.asInstanceOf[ListItem[T]].getIndex
        val destIndex = sortIndex
        log info "Moving element from position: " + srcIndex + " to position: " + destIndex
        model setObject swap(model.getObject, srcIndex, destIndex)
        log debug "Moved list item from index: " + srcIndex + " to index: " + destIndex
        // source is the parent container of the ListView
        target add source
      }
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
  def swap[Y](buf: JArrayList[Y], before: Int, after: Int) = {
    if (before != after)
      buf.add(after, buf.remove(before))
    buf
  }
}