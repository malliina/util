package tests

import com.malliina.collection.{BFSTraverser, DFSTraverser, TreeTraverser}
import org.scalatest.FunSuite

class TreeTests extends FunSuite {
  def dfs = DFSTraverser

  def bfs = BFSTraverser

  def children(i: Int) = i match {
    case 1 => List(2, 3)
    case 2 => List(4, 5)
    case 3 => List(111)
    case 4 => List(1)
    case 5 => List(6, 111)
    case 6 => List(7, 8)
    case _ => Nil
  }

  test("bfs returns shortest path, dfs returns left-biased path") {
    def findPath(t: TreeTraverser) = t.findPath[Int](_ == 111, 1, children)
    assert(findPath(bfs) === List(1, 3, 111))
    assert(findPath(dfs) === List(1, 2, 5, 111))
  }

  test("finds root") {
    def findPath(t: TreeTraverser) = t.findPath[Int](_ == 2, 123, _ => Nil)
    assert(findPath(bfs) === Nil)
    assert(findPath(dfs) === Nil)
  }

  test("finds root, vol 2") {
    def findPath(t: TreeTraverser) = t.findPath[Int](_ == 2, 2, children)
    assert(findPath(bfs) === List(2))
    assert(findPath(dfs) === List(2))
  }

  test("finds path") {
    def findPath(t: TreeTraverser) = t.findPath[Int](_ == 6, 1, children)
    val expected = List(1, 2, 5, 6)
    assert(findPath(bfs) === expected)
    assert(findPath(dfs) === expected)
  }

  test("does not find nonexistent path") {
    def findPath(t: TreeTraverser) = t.findPath[Int](_ == 42, 1, children)
    assert(findPath(bfs) === Nil)
    assert(findPath(dfs) === Nil)
  }

  test("does not find nonexistent path, vol 2") {
    def findPath(t: TreeTraverser) = t.findPath[Int](_ == 42, 666, children)
    assert(findPath(bfs) === Nil)
    assert(findPath(dfs) === Nil)
  }
}
