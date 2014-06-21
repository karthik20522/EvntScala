package com.kufli.common

class EvntScalaException(msg: String) extends RuntimeException(msg)

object EvntScalaException extends com.kufli.log.Logging {

  def create(msg: String): EvntScalaException = {
    log.error(msg)
    new EvntScalaException(msg)
  }

  def create(msg: String, cause: Throwable) = {
    log.error(msg, cause)
    new EvntScalaException(msg).initCause(cause)
  }
}
