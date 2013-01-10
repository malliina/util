package com.mle.actor.tests

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSuite}
import akka.actor._
import concurrent._
import scala.concurrent.duration._
import akka.util.Timeout
import akka.pattern.ask
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}

/**
 *
 * @author mle
 */
class ActorTest extends TestKit(ActorSystem("test-system")) with ImplicitSender with FunSuite with BeforeAndAfter with BeforeAndAfterAll {
  implicit val timeout = Timeout(36500 days)
  val testMessage = "Hello, world"

  override def afterAll {
    system.shutdown()
  }

  test("actor can play tiki taka football") {
    val wall = ActorDSL.actor(system)(new TikiTakaActor)
    wall ! testMessage
    expectMsg(testMessage)
  }
  test("actor receives reply within reasonable time period") {
    val wall = TestActorRef(new TikiTakaActor)
    val result = Await.result((wall ? testMessage), 1000 milliseconds).asInstanceOf[String]
    assert(result === testMessage)
  }
  test("actor receives reply within reasonable time period ver 2") {
    val wall = ActorDSL.actor(system)(new TikiTakaActor)
    wall ! testMessage
    expectMsg(1000 milliseconds, testMessage)
  }
  test("actor times out waiting for reply after unreasonable time period has passed") {
    val wall = ActorDSL.actor(system)(new TikiTakaActor)
    intercept[TimeoutException] {
      Await.result((wall ? testMessage), 100 milliseconds).asInstanceOf[String]
    }
  }

  class TikiTakaActor extends Actor {
    def receive = {
      case msg =>
        Thread sleep 500
        sender ! msg
    }
  }

}
