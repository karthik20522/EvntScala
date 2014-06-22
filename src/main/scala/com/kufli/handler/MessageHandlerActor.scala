package com.kufli.handler

import akka.actor.Actor
import com.kufli.amqp.MQMessage
import akka.actor.ActorRef
import akka.actor.ActorLogging
import com.kufli.log.ReceiveLogger
import com.kufli.common.EvntScalaException
import com.kufli.log.Logging

class MessageHandlerActor extends Actor with ReceiveLogger with Logging {

  def receive = logMessage orElse {
    case msg: MQMessage => {
      log.info(msg.toString())
    }
    case _ => throw EvntScalaException.create("Unknown message type")
  }
}