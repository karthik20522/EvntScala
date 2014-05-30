package com.kufli.handler

import akka.actor.Actor
import com.kufli.amqp.MQMessage
import akka.actor.ActorRef

class MessageHandlerActor(amqpAckActor: ActorRef) extends Actor {

  def receive = {
    case msg: MQMessage => {
      amqpAckActor ! msg.deliveryTag
    }
    case _ => throw new Exception("Unknown message type")
  }
}