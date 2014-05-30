package com.kufli.elasticsearch

import spray.client.pipelining._
import org.json4s._
import org.json4s.JsonAST.{ JArray, JObject, JValue }
import org.json4s.native._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

import akka.actor.{ ActorLogging, Actor }
import com.typesafe.config.ConfigFactory
import scala.util.{ Success, Failure }
import com.kufli.JsonSupport

class ElasticsearchActor extends Actor with JsonSupport {

  import ESCommands._
  import context.dispatcher

  val config = ConfigFactory.load().getConfig("elasticsearch")

  val url = config.getString("url")
  val pipeline = addHeader("Content-Type", "application/json") ~> sendReceive ~> unmarshal[JValue]

  def receive = {
    case Index(indexName, typeName, data) => {
      val originalSender = context.sender
      val uri = s"$url/$indexName/$typeName"
      pipeline(Post(uri, compact(render(data)))) onComplete {
        case Success(response) =>
          originalSender ! "OK"
        case Failure(ex) =>
          throw new Exception(ex)
      }
    }
    case Query(indexName, typeName, query) => {
      val originalSender = context.sender
      val uri = s"$url/$indexName/$typeName/_search"

      pipeline(Get(uri, query)) onComplete {
        case Success(response) =>
          val objects = extractSourceObjects(response)
          originalSender ! Response(objects)
        case Failure(ex) =>
          throw new Exception(ex)
      }
    }
  }

  def extractSourceObjects(response: JsonAST.JValue): List[JObject] = {
    val hits = response \ "hits" \ "hits"
    for {
      JArray(list) <- hits
      JObject(hit) <- list
      JField("_source", source: JObject) <- hit
    } yield source
  }
}