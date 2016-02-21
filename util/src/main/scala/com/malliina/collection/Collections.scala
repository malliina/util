package com.malliina.collection

import scala.annotation.tailrec

object Collections extends Collections

trait Collections {
  @tailrec
  final def findFirst[T, U](ts: List[T], map: T => U, predicate: U => Boolean): Option[U] = {
    ts match {
      case Nil => None
      case head :: tail =>
        val candidate = map(head)
        if (predicate(candidate)) Option(candidate)
        else findFirst(tail, map, predicate)
    }
  }
}
