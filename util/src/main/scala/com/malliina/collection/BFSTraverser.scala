package com.malliina.collection

object BFSTraverser extends BFSTraverser

trait BFSTraverser extends TreeTraverser {
  def findPath[T](isTarget: T => Boolean, root: T, children: T => List[T]): List[T] =
    findPathAcc(isTarget, List(root :: Nil), children)

  private def findPathAcc[T](isTarget: T => Boolean, threads: List[List[T]], children: T => List[T]): List[T] = {
    if (threads.isEmpty) {
      Nil
    } else {
      val maybePath = threads.find(thread => thread.headOption.exists(isTarget))
      maybePath.map(_.reverse).getOrElse {
        val newThreads = for {
          thread <- threads
          head <- thread.headOption.toList
          child <- children(head).filterNot(thread.contains)
        } yield child :: thread
        val nonEmptyThreads = newThreads.filter(_.nonEmpty)
        findPathAcc(isTarget, nonEmptyThreads, children)
      }
    }
  }
}
