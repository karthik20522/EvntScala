package com.kufli.amqp

import akka.actor._
import com.rabbitmq.client.{ QueueingConsumer, Channel }

class AMQPListenerActor(queueName: String, listeningChannel: Channel, msgHandler: ActorRef) extends Actor {

  override def preStart = self ! "init"

  def receive = {
    case "init" => startReceving
  }

  def startReceving = {
    val consumer = new QueueingConsumer(listeningChannel)

    listeningChannel.basicConsume(queueName, false, consumer)
    listeningChannel.basicQos(20)

    while (true) {
      val delivery = consumer.nextDelivery()
      val msg = new String(delivery.getBody())
      msgHandler ! MQMessage(delivery.getEnvelope().getDeliveryTag(), msg)
    }
  }
}
