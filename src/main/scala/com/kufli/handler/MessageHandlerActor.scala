package com.kufli.handler

import akka.actor.Actor
import com.kufli.amqp.MQMessage
import akka.actor.ActorRef
import akka.actor.ActorLogging
import com.kufli.common.ReceiveLogger
import com.kufli.common.EvntScalaException

class MessageHandlerActor(amqpAckActor: ActorRef) extends Actor with ActorLogging with ReceiveLogger {

  def receive = logMessage orElse {
    case msg: MQMessage => {
      log.info(msg.data)
      amqpAckActor ! msg.deliveryTag
    }
    case _ => throw EvntScalaException.create("Unknown message type")
  }
}