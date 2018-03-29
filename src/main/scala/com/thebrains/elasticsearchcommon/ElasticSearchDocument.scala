package com.thebrains.elasticsearchcommon

import scala.util.parsing.json.JSONFormat

trait ElasticSearchDocument {
  def toDocument: Map[String, Any]

  def getId: String

  def json: String = {
    scala.util.parsing.json.JSONObject(this.toDocument).toString(JSONFormat.defaultFormatter)
  }
}
