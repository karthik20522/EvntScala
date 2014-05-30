package com.kufli.amqp

case class MQMessage(deliveryTag: Long, data: String)