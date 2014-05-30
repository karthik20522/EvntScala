package com.kufli.db

import scala.concurrent.Future
import org.joda.time.LocalDate
import com.github.mauricio.async.db.{ RowData, Connection }
import com.github.mauricio.async.db.util.ExecutorServiceUtils.CachedExecutionContext

/*CREATE TABLE messages
(
  id bigserial NOT NULL,
  content character varying(255) NOT NULL,
  moment date NOT NULL,
  CONSTRAINT bigserial_column_pkey PRIMARY KEY (id )
);*/

object MessageRepository {
  val Insert = "INSERT INTO messages (content,moment) VALUES (?,?) RETURNING id"
  val Update = "UPDATE messages SET content = ?, moment = ? WHERE id = ?"
  val SelectOne = "SELECT id, content, moment FROM messages WHERE id = ?"
}

class MessageRepository(pool: Connection) {

  import MessageRepository._

  def save(m: Message): Future[Message] = {
    m.id match {
      case Some(id) => pool.sendPreparedStatement(Update, Array(m.content, m.moment, id)).map {
        queryResult => m
      }
      case None => pool.sendPreparedStatement(Insert, Array(m.content, m.moment)).map {
        queryResult => new Message(Some(queryResult.rows.get(0)("id").asInstanceOf[Long]), m.content, m.moment)
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
      id = Some(row("id").asInstanceOf[Long]),
      content = row("content").asInstanceOf[String],
      moment = row("moment").asInstanceOf[LocalDate])
  }
}