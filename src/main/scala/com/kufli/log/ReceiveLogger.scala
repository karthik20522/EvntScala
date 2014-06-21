package com.kufli.log

import akka.actor.Actor
import com.kufli.common.EvntScalaException

trait ReceiveLogger {
  this: Actor with Logging =>

  def logMessage: Receive = new Receive {
    def isDefinedAt(x: Any) = {
      log.debug(s"\nSender -> ${sender.path.toString()}\nMessage -> $x\n")
      false
    }
    def apply(x: Any) = throw EvntScalaException.create("Unsupported Operation", new UnsupportedOperationException)
  }
}