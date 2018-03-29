package com.thebrains.elasticsearchcommon

import java.util.concurrent.TimeUnit

import com.sksamuel.elastic4s.{RefreshPolicy, TypesApi}
import com.sksamuel.elastic4s.http.{RequestFailure, RequestSuccess}
import com.sksamuel.elastic4s.indexes.IndexDefinition
import com.sksamuel.elastic4s.mappings.FieldDefinition

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.Duration

abstract class ElasticSearchWrapper[T <: ElasticSearchDocument](
  es: ElasticSearchClient,
  indexName: String,
  documentType: String
) extends TypesApi {
  val duration: Duration = Duration(10, "minutes")
  var lastResult: Option[Either[RequestFailure, RequestSuccess[_]]] = None

  /**
    * textField("country"), textField("capital")
    *
    * @return
    */
  protected def getIndexMapping: Seq[FieldDefinition]

  def removeIndex(implicit execC: ExecutionContext): ElasticSearchWrapper[T] = {
    import com.sksamuel.elastic4s.http.ElasticDsl._
    this.lastResult = Some(es
      .client
      .execute(
        deleteIndex(indexName)
      )
      .await(duration)
    )
    this
  }

  def createMyIndex(implicit execC: ExecutionContext): ElasticSearchWrapper[T] = {
    import com.sksamuel.elastic4s.http.ElasticDsl._
    this.lastResult = Some(es
      .client
      .execute {
        createIndex(indexName)
          .refreshInterval(Duration(5, "seconds"))
          //          .singleReplica()
          .singleShard()
          .mappings(
            mapping(documentType).as(
              getIndexMapping
            )
          )
          .waitForActiveShards(1)
      }
      .await(duration)
    )
    this
  }

  def insertDocuments(
    documents: Seq[T]
  )(implicit execC: ExecutionContext): ElasticSearchWrapper[T] = {
    import com.sksamuel.elastic4s.http.ElasticDsl._
    val docs: Seq[IndexDefinition] = documents.map { document =>
      indexInto(indexName / documentType)
        .id(document.getId)
        .doc(document.json)
        //        .fields(document.toDocument)
        //        .source(document.json)
        .refresh(RefreshPolicy.IMMEDIATE)
    }

    this.lastResult = Some(es
      .client
      .execute {
        //      index into "test" -> "events" doc StringDocumentSource(monitorJson)

        //        docs.head
        bulk(docs)
//          .timeout(Duration("10000n"))
          .refresh(RefreshPolicy.IMMEDIATE)
          .waitForRefresh()
        //        refreshIndex(indexName)
      }
      .await(duration)
    )

    this
  }

  def close(): Unit = {
    es.closeConnection()
  }

  //  def chainOne[A](
  //    f: () => Future[Either[RequestFailure, RequestSuccess[A]]],
  //    onFail: (RequestFailure) => Unit,
  //    onSuccess: (RequestSuccess[A]) => Unit
  //  )(implicit es: ExecutionContext): () => Unit = {
  //    chainAll(
  //      Seq(f),
  //      (failures: Seq[RequestFailure]) => onFail(failures.head),
  //      (successes: Seq[RequestSuccess[A]]) => onSuccess(successes.head)
  //    )
  //  }
  //
  //  def chainAll[A](
  //    tasks: Seq[() => Future[Either[RequestFailure, RequestSuccess[A]]]],
  //    onFail: (Seq[RequestFailure]) => Unit,
  //    onSuccess: (Seq[RequestSuccess[A]]) => Unit
  //  )(implicit es: ExecutionContext): () => Unit = {
  //    import com.sksamuel.elastic4s.http.ElasticDsl._
  //    () => {
  //      val duration: Duration = Duration(10, "minutes")
  //      val a = tasks.map(f => f()).map(f => f.await(duration))
  //      val tmp = a.map { f =>
  //        var success: Option[RequestSuccess[A]] = None
  //        var failure: Option[RequestFailure] = None
  //        f.fold(fa => failure = Some(fa), fb => success = Some(fb))
  //        (success, failure)
  //      }
  //      val allSuccess = tmp.flatMap(_._1)
  //      val allFailure = tmp.flatMap(_._2)
  //      if (allFailure.nonEmpty) {
  //        onFail(allFailure.distinct)
  //      }
  //      if (allSuccess.nonEmpty) {
  //        onSuccess(allSuccess.distinct)
  //      }
  //    }
  //  }
}
