package com.malliina.collection

trait TreeTraverser {
  /** Computes the path to an element for which `isTarget` is true, using the tree comprised of `root` and `children`.
    *
    * @param isTarget predicate
    * @param root     start
    * @param children children of an element
    * @tparam T type of element
    * @return a path or an empty List
    */
  def findPath[T](isTarget: T => Boolean, root: T, children: T => List[T]): List[T]

  def pathTo[T](target: T, root: T, children: T => List[T]): List[T] =
    findPath[T](_ == target, root, children)
}
