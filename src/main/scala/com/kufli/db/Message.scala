package com.kufli.db

import org.joda.time.LocalDate

case class Message(id: Option[Long], content: String, moment: LocalDate = LocalDate.now())