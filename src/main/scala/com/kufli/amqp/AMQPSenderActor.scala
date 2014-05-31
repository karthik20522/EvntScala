package com.kufli.amqp

import akka.actor._
import com.rabbitmq.client.{ QueueingConsumer, Channel }
import akka.event.LoggingReceive
import com.kufli.common.ReceiveLogger
import com.kufli.common.EvntScalaException

class AMQPSenderActor(exchangeName: String, routingKey: String, sendingChannel: Channel) extends Actor with ActorLogging with ReceiveLogger {

  def receive = logMessage orElse {
    case deliveryTag: Long => sendingChannel.basicAck(deliveryTag, false)
    case msg: String => sendingChannel.basicPublish(exchangeName, routingKey, null, msg.getBytes())
    case _ => throw throw EvntScalaException.create("Unknown message type")
  }
}