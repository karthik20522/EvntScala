package com.kufli.amqp

import akka.actor._
import com.rabbitmq.client.{ QueueingConsumer, Channel }
import com.kufli.log.ReceiveLogger
import com.kufli.common.EvntScalaException
import com.kufli.log.Logging

class AMQPListenerActor(queueName: String, listeningChannel: Channel) extends Actor with IAMQPListener with ReceiveLogger with Logging {

  def receive = logMessage orElse {
    case "init" => startReceiving(queueName, listeningChannel, sender)
    case _ => throw EvntScalaException.create("Unknown message")
  }
}

trait IAMQPListener { actor: Actor =>

  def startReceiving(queueName: String, listeningChannel: Channel, msgHandler: ActorRef) = {
    val consumer = new QueueingConsumer(listeningChannel)
    listeningChannel.basicConsume(queueName, true, consumer)
    listeningChannel.basicQos(20)

    while (true) {
      val delivery = consumer.nextDelivery()
      val msg = new String(delivery.getBody())
      val mqMessage = MQMessage(MessageProperties(delivery.getProperties, delivery.getEnvelope().getDeliveryTag()), msg)
      msgHandler ! mqMessage
    }
  }
}
