package com.malliina.collection

object DFSTraverser extends DFSTraverser

trait DFSTraverser extends TreeTraverser {
  def findPath[T](isTarget: T => Boolean, root: T, children: T => List[T]): List[T] =
    findPathAcc(isTarget, root, children, Nil)

  private def findPathAcc[T](isTarget: T => Boolean,
                             root: T,
                             children: T => List[T],
                             accumulatedPath: List[T]): List[T] = {
    val newAcc = root :: accumulatedPath
    if (isTarget(root)) {
      newAcc.reverse
    } else {
      val unseenChildren = children(root) filterNot accumulatedPath.contains
      Collections.findFirst[T, List[T]](unseenChildren, findPathAcc(isTarget, _, children, newAcc), _.nonEmpty)
        .getOrElse(Nil)
    }
  }
}
