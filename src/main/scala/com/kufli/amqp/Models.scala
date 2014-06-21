package com.kufli.amqp

import com.rabbitmq.client.AMQP

case class MQMessage(messageProperties: MessageProperties, data: String)
case class Ack(deliveryTag: Long)

case class MessageProperties(
  contentType: String = "application/json",
  contentEncoding: String = null,
  headers: Map[String, AnyRef] = Map(),
  priority: Int = 0,
  correlationId: String = null,
  replyTo: String = null,
  expiration: String = null,
  messageId: String = null,
  userId: String = null,
  deliveryTag: Long = 0)

object MessageProperties {
  def apply(props: AMQP.BasicProperties, dTagId: Long): MessageProperties =
    MessageProperties(
      contentType = props.getContentType(),
      contentEncoding = props.getContentEncoding(),
      headers = null,
      priority = integer2int(props.getPriority(), 0),
      correlationId = props.getCorrelationId(),
      replyTo = props.getReplyTo(),
      expiration = props.getExpiration(),
      messageId = props.getMessageId(),
      userId = props.getUserId(),
      deliveryTag = dTagId)

  private def integer2int(value: Integer, defaultValue: Int): Int = if (value != null) value else defaultValue
}