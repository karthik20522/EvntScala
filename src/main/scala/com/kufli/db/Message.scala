package com.kufli.db

import org.joda.time.LocalDate

case class Message(guid: Option[String], id: Long, content: String, timestamp: LocalDate = LocalDate.now())