package com.kufli.amqp

import akka.actor._
import com.rabbitmq.client.{ QueueingConsumer, Channel }
import akka.event.LoggingReceive
import com.kufli.log.ReceiveLogger
import com.kufli.common.EvntScalaException
import com.kufli.log.Logging

class AMQPSenderActor(exchangeName: String, routingKey: String, sendingChannel: Channel) extends Actor with ReceiveLogger with Logging {

  def receive = logMessage orElse {
    case deliveryTag: Long => sendingChannel.basicAck(deliveryTag, false)
    case msg: String => sendingChannel.basicPublish(exchangeName, routingKey, null, msg.getBytes())
    case _ => throw throw EvntScalaException.create("Unknown message type")
  }
}