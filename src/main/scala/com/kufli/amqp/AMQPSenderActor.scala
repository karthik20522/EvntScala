package com.kufli.amqp

import akka.actor._
import com.rabbitmq.client.{ QueueingConsumer, Channel }

class AMQPSenderActor(exchangeName: String, routingKey: String, sendingChannel: Channel) extends Actor {

  def receive = {
    case deliveryTag: Long => sendingChannel.basicAck(deliveryTag, false)
    case msg: String => sendingChannel.basicPublish(exchangeName, routingKey, null, msg.getBytes())
    case _ => throw new Exception("Unknown message type")
  }
}