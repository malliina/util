package com.mle.tests

import org.scalatest.FunSuite
import xml.NodeSeq
import sys.process.Process
import java.nio.file.{Files, Paths}
import com.mle.util.FileUtilities
import io.Source

/**
 *
 * @author mle
 */
class RandomTests extends FunSuite {
  test("XML concatenation") {
    val emptyXml = scala.xml.Null
    val xml1: NodeSeq = (<a>baba</a>)
    val xml2: NodeSeq = (<a>bubu</a>)
    val xml3 = xml1 ++ xml2
    val xml = <main>
      {xml1}{xml2}
    </main>
    println(xml)

    val xmlSeq = Seq(xml1, xml2).foldLeft(NodeSeq.Empty)((sum, elem) => sum ++ elem)
    println(xmlSeq)
  }
  test("Command execution") {
    val cmd = """C:\Program Files (x86)\Launch4j\launch4jc.exe"""
    val exe = Paths get cmd
    assert(Files.isExecutable(exe))
    Process(cmd, Some(Paths.get("C:\\Stuff").toFile)).! match {
      case 0 => println("Success")
      case _ => println("Failure")
    }
  }
  test("File writing") {
    val content = "ooooo"
    val path = FileUtilities.writerTo("test.txt")(writer => writer.println(content))
    println("Wrote to: " + path.toAbsolutePath)
    val lines = Source.fromFile(path.toFile).getLines().mkString(",")
    assert(lines === content)
  }
}
