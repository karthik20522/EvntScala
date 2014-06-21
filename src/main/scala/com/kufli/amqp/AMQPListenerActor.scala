package com.kufli.amqp

import akka.actor._
import com.rabbitmq.client.{ QueueingConsumer, Channel }
import com.kufli.common.ReceiveLogger
import com.kufli.common.EvntScalaException
import com.kufli.common.Logging

class AMQPListenerActor(queueName: String, listeningChannel: Channel, msgHandler: ActorRef) extends Actor with Logging {

  override def preStart = self ! "init"

  def receive = {
    case "init" => startReceving
    case _ => throw EvntScalaException.create("Unknown message")
  }

  def startReceving = {
    val consumer = new QueueingConsumer(listeningChannel)
    listeningChannel.basicConsume(queueName, false, consumer)
    listeningChannel.basicQos(20)

    while (true) {
      val delivery = consumer.nextDelivery()
      val msg = new String(delivery.getBody())
      msgHandler ! MQMessage(MessageProperties(delivery.getProperties, delivery.getEnvelope().getDeliveryTag()), msg)
    }
  }
}
