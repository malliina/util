package com.mle.xml.tests

import org.scalatest.FunSuite
import xml.{NodeSeq, Elem}

/**
 *
 * @author mle
 */
class XmlTests extends FunSuite {
  test("XML concatenation") {
    val emptyXml = scala.xml.Null
    val xml1:NodeSeq = (<a>baba</a>)
    val xml2:NodeSeq = (<a>bubu</a>)
    val xml3 = xml1 ++ xml2
    val xml = <main>
      {xml1}{xml2}
    </main>
    println(xml)

    val xmlSeq = Seq(xml1,xml2).foldLeft(NodeSeq.Empty)((sum,elem) => sum ++ elem)
    println(xmlSeq)
  }
}
