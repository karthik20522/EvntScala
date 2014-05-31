package com.kufli.db

import scala.concurrent.Future
import org.joda.time.LocalDate
import com.github.mauricio.async.db.{ RowData, Connection }
import com.github.mauricio.async.db.util.ExecutorServiceUtils.CachedExecutionContext

/*CREATE TABLE messages
(
  `GUID` CHAR(36) NOT NULL,
  id INTEGER NOT NULL,
  content BLOB NOT NULL,
  timestamp DATE NOT NULL,
  PRIMARY KEY (`GUID`),
  INDEX `messages_id` (`id`),
  INDEX `messages_timeStamp` (`timestamp`)
);*/

object MessageRepository {
  val Insert = "INSERT INTO messages (guid, id, content,timestamp) VALUES (?,?,?,?)"
  val Update = "UPDATE messages SET content = ?, moment = ? WHERE guid = ?"
  val SelectOne = "SELECT id, content, moment FROM messages WHERE id = ?"
}

class MessageRepository(pool: Connection) {

  import MessageRepository._

  def save(m: Message): Future[Message] = {
    m.guid match {
      case Some(guid) => pool.sendPreparedStatement(Update, Array(m.content, m.timestamp, guid)).map {
        queryResult => m
      }
      case None => {
        val guid = java.util.UUID.randomUUID.toString
        pool.sendPreparedStatement(Insert, Array(guid, m.id, m.content, m.timestamp)).map {
          queryResult => new Message(Some(guid), m.id, m.content, m.timestamp)
        }
      }
    }
  }

  def find(id: Long): Future[Option[Message]] = {
    pool.sendPreparedStatement(SelectOne, Array[Any](id)).map {
      queryResult =>
        queryResult.rows match {
          case Some(rows) => {
            Some(rowToMessage(rows.apply(0)))
          }
          case None => None
        }
    }
  }

  private def rowToMessage(row: RowData): Message = {
    new Message(
      guid = Some(row("guid").asInstanceOf[String]),
      id = row("id").asInstanceOf[Long],
      content = row("content").asInstanceOf[String],
      timestamp = row("timestamp").asInstanceOf[LocalDate])
  }
}