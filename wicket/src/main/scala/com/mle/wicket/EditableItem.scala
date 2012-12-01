package com.mle.wicket

/**
 * An item that has a unique item, such as a name or an ID.
 *
 * @author mle
 */
trait EditableItem[T] {
  /**
   * @return the unique value
   */
  def id: T
  /**
   * Hack that compares two items.
   *
   * Since <code>id</code> uniquely identifies the item, we simplify equals.
   *
   * Use cases:
   * Wicket *Choice components compare the selected item
   * with the choices using <code>equals</code> to determine if anything is "selected"
   *
   * This method is bullshit in principle, but works in my limited reality.
   *
   * @param other compareTo
   * @return true if equals, false otherwise
   */
  override def equals(other: Any) = other match {
    case otherItem: EditableItem[T] => otherItem.id == id
    case anythingElse => false
  }
}
