package com.kufli

import akka.actor.Props
import akka.actor.ActorSystem
import com.kufli.handler.MessageHandlerActor
import com.kufli.amqp._
import com.kufli.db.DBConnection
import scala.util.Try

object EvntScala extends App {
  sys addShutdownHook (shutdown)

  override def main(args: Array[String]) {
    val system = ActorSystem("evntScala")

    val amqpChannel = AMQPConnection.getConnection().createChannel()
    amqpChannel.exchangeDeclare("evntEx", "direct", true);
    amqpChannel.queueDeclare("evntQ", true, false, false, null)
    amqpChannel.queueBind("evntQ", "evntEx", "public.evnt.scala")

    val amqpSender = system.actorOf(Props(new AMQPSenderActor("evntEx", "public.evnt.scala", amqpChannel)), name = "amqpSender")
    val messageHandler = system.actorOf(Props(new MessageHandlerActor(amqpSender)), name = "messageHandler")
    val amqpListener = system.actorOf(Props(new AMQPListenerActor("evntQ", amqpChannel, messageHandler)), name = "amqpListener")
  }

  private def shutdown {
    DBConnection.close
    AMQPConnection.close
  }
}