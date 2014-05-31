package com.kufli.common

class EvntScalaException(msg: String) extends RuntimeException(msg)

object EvntScalaException {

  def create(msg: String): EvntScalaException = new EvntScalaException(msg)

  def create(msg: String, cause: Throwable) = new EvntScalaException(msg).initCause(cause)
}
