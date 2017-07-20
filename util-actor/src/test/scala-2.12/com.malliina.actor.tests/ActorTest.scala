package com.malliina.actor.tests

import akka.actor._
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuiteLike}

import scala.concurrent._
import scala.concurrent.duration.DurationInt

class ActorTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike with ImplicitSender with BeforeAndAfter with BeforeAndAfterAll {
  implicit val timeout = Timeout(100.days)
  val testMessage = "Hello, world"

  override def afterAll {
    Await.result(system.terminate(), 10.seconds)
  }

  test("actor can play tiki taka football") {
    val wall = system.actorOf(Props[TikiTakaActor])
    wall ! testMessage
    expectMsg(testMessage)
  }

  test("actor receives reply within reasonable time period") {
    val wall = system.actorOf(Props[TikiTakaActor])
    val result = Await.result(wall ? testMessage, 1000.milliseconds).asInstanceOf[String]
    assert(result === testMessage)
  }

  test("actor receives reply within reasonable time period ver 2") {
    val wall = system.actorOf(Props[TikiTakaActor])
    wall ! testMessage
    expectMsg(1000.milliseconds, testMessage)
  }

  test("actor times out waiting for reply after unreasonable time period has passed") {
    val wall = system.actorOf(Props[TikiTakaActor])
    intercept[TimeoutException] {
      Await.result(wall ? testMessage, 100.milliseconds).asInstanceOf[String]
    }
  }
}

class TikiTakaActor extends Actor {
  def receive = {
    case msg =>
      Thread sleep 500
      sender ! msg
  }
}
