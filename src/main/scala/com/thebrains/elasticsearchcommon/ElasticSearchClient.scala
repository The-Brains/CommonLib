package com.thebrains.elasticsearchcommon

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.HttpClient

class ElasticSearchClient(host: String = "localhost", port: Int = 9200) {
  val client = HttpClient(ElasticsearchClientUri(host, port))

  def closeConnection(): Unit = {
    client.close()
  }
}
