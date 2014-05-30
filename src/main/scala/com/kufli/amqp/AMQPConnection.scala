package com.kufli.amqp

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.typesafe.config.ConfigFactory
import com.rabbitmq.client.Channel

object AMQPConnectionConfig {
  private val config = ConfigFactory.load()

  val RABBITMQ_HOST = config.getString("rabbitmq.uri")
}

object AMQPConnection {
  private val connection: Connection = null

  /**
   * Return a connection if one doesn't exist. Else create
   * a new one
   */
  def getConnection(): Connection = {
    connection match {
      case null => {
        val factory = new ConnectionFactory()
        factory.setUri(AMQPConnectionConfig.RABBITMQ_HOST)
        factory.setRequestedHeartbeat(30)
        factory.setAutomaticRecoveryEnabled(true)
        factory.setTopologyRecoveryEnabled(true)
        factory.setConnectionTimeout(5000)
        factory.newConnection()
      }
      case _ => connection
    }
  }

  def close = connection match {
    case x if (x != null) => connection.close
  }
}