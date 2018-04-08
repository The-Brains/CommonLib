package com.thebrains.sparkcommon

import com.thebrains.utils.Config
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession

abstract class SparkJob[T <: Config](spark: SparkSession) {
  implicit val sk: SparkSession = spark
  protected val log: Logger = Logger.getLogger(spark.sparkContext.appName)

  def run(config: T): Unit
}
