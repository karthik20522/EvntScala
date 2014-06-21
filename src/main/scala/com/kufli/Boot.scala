package com.kufli

import akka.actor.{ Props, ActorSystem }
import scala.concurrent.duration._
import scala.util.Random
import com.kufli.cluster.Message
import com.kufli.log.Logging
import com.kufli.cluster.BroadcastActor

object EvntScala extends App with Logging {
    val system = ActorSystem("evntScala", AkkaConfig.config)
    val broadcaster = system.actorOf(Props[BroadcastActor], name = "broadcast")
    log.info("EvntScala Started")

     implicit val executor = system.dispatcher
    system.scheduler.schedule(0 seconds, 10 seconds) {
    val words = Random.shuffle(
      List("peter", "piper", "picked", "a", "peck", "of", "pickled", "pepper"))
    broadcaster ! Message(words mkString " ")
   } 
  

  /*  val amqpChannel = AMQPConnection.getConnection().createChannel()
  amqpChannel.exchangeDeclare("evntEx", "direct", true);
  amqpChannel.queueDeclare("evntQ", true, false, false, null)
  amqpChannel.queueBind("evntQ", "evntEx", "public.evnt.scala")

  val amqpSender = system.actorOf(Props(new AMQPSenderActor("evntEx", "public.evnt.scala", amqpChannel)), name = "amqpSender")
  val messageHandler = system.actorOf(Props(new MessageHandlerActor(amqpSender)), name = "messageHandler")
  val amqpListener = system.actorOf(Props(new AMQPListenerActor("evntQ", amqpChannel, messageHandler)), name = "amqpListener")*/
}