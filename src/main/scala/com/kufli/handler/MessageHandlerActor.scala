package com.kufli.handler

import akka.actor.Actor
import com.kufli.amqp.MQMessage
import akka.actor.ActorRef
import akka.actor.ActorLogging
import com.kufli.common.ReceiveLogger
import com.kufli.common.EvntScalaException
import com.kufli.common.Logging

class MessageHandlerActor(amqpAckActor: ActorRef) extends Actor with ReceiveLogger with Logging {

  def receive = logMessage orElse {
    case msg: MQMessage => {
      amqpAckActor ! msg.messageProperties.deliveryTag
    }
    case _ => throw EvntScalaException.create("Unknown message type")
  }
}