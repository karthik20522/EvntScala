package com.kufli.handler

import akka.actor.Actor
import com.kufli.amqp.MQMessage
import akka.actor.ActorRef
import akka.actor.ActorLogging
import com.kufli.log.ReceiveLogger
import com.kufli.common.EvntScalaException
import com.kufli.log.Logging
import com.kufli.cluster.MasterIdList

/*
 * NOTE: This is like a supervisor actor that receives masterId and asset data from the singleton cluster
 * 		 This actor creates the actual masterId actor and handles all exceptions and restarts 
 */
class MessageHandlerActor extends Actor with ReceiveLogger with Logging {

  def receive = logMessage orElse {

    case (masterId: String, msg: MQMessage) => {

      //TODO: Spawn new child masterId actor that does the processing - 
      //	  Write to db, elasticsearch and event/command sourcing

      log.info(msg.data)
    }

    case _ => throw EvntScalaException.create("Unknown message type")
  }
}