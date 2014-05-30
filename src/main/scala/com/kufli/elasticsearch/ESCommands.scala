package com.kufli.elasticsearch

import org.json4s.JsonAST._

object ESCommands {
  case class Query(indexName: String, typeName: String, query: Option[JValue])
  case class Response(objects: Seq[JObject])
  case class Index(indexName: String, typeName: String, data: JObject)
}