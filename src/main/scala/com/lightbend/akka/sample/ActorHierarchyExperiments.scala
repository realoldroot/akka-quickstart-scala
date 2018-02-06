package com.lightbend.akka.sample

import akka.actor.{Actor, ActorSystem, Props}

import scala.io.StdIn

/**
  * ${DESCRIPTION}
  *
  * @author zhengenshen 2018-02-06 17:25
  */
class PrintMyActorRefActor extends Actor {
  override def receive: Receive = {
    case "printit" =>
      val secondRef = context.actorOf(Props.empty, "second-actor")
      println(s"Second: $secondRef")
  }
}

object ActorHierarchyExperiments extends App {
  val system = ActorSystem("testSystem")

  val firstRef = system.actorOf(Props[PrintMyActorRefActor], "first-actor")

  println(s"First: $firstRef")

  firstRef ! "printit"

  println(">>> Press ENTER to exit <<<")
  try StdIn.readLine()
  finally system.terminate()
}


// stat and stop

class StartStopActor1 extends Actor {

  override def preStart(): Unit = {
    println("first started")
    context.actorOf(Props[StartStopActor2], "second")
  }


  override def postStop(): Unit = println("first stop")

  override def receive: Receive = {
    case "stop" => context.stop(self)
  }
}

class StartStopActor2 extends Actor {
  override def preStart(): Unit = println("second started")

  override def postStop(): Unit = println("second stopped")

  override def receive: Receive = Actor.emptyBehavior
}

object startStop extends App {

  val system = ActorSystem("startStop")

  val first = system.actorOf(Props[StartStopActor1], "first")

  first ! "stop"
}

//  exception

class SupervisingActor extends Actor {

  val child = context.actorOf(Props[SupervisedActor], "supervised-actor")

  override def receive: Receive = {
    case "failChild" => child ! "fail"
  }
}

class SupervisedActor extends Actor {
  override def preStart(): Unit = println("supervised actor started")


  override def preRestart(reason: Throwable, message: Option[Any]): Unit = println("pre restart")


  override def postRestart(reason: Throwable): Unit = println("post restart")

  override def postStop(): Unit = println("supervised actor stoped")

  override def receive: Receive = {
    case "fail" =>
      println("supervised actor fails now")
      throw new Exception("I failed!")
  }
}

object ActorException extends App {
  val system = ActorSystem("actorException")
  val supervisingActor = system.actorOf(Props[SupervisingActor], "supervising-acotr")

  supervisingActor ! "failChild"

}