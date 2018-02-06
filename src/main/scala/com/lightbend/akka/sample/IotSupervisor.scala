package com.lightbend.akka.sample

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import scala.io.StdIn

/**
  *
  * @author zhengenshen 2018-02-06 19:41
  */
object IotSupervisor {

  def props(): Props = Props(new IotSupervisor)

}

class IotSupervisor extends Actor with ActorLogging {

  override def preStart(): Unit = log.info("IoT Application started")

  override def postStop(): Unit = log.info("IoT Application stopped")

  override def receive: Receive = Actor.emptyBehavior
}

object IotApp {
  def main(args: Array[String]): Unit = {

    val system = ActorSystem("iot-system")

    try {
      // 创建高层主管
      val supervisor = system.actorOf(IotSupervisor.props(), "iot-supervisor")

      //阻塞线程输出enter后退出
      StdIn.readLine()
    } finally {
      system.terminate()
    }

  }
}

