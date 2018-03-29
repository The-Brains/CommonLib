package com.thebrains.sparkcommon

import com.thebrains.utils.Config
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

trait SparkMain[T <: SparkJob[C], C <: Config] {
  def getName: String

  protected def instantiateJob: (SparkSession) => T

  protected def instantiateConfig: (Seq[String]) => C

  def main(args: Array[String]): Unit = {
    val configManager: C = instantiateConfig(args)
    configManager.init()

    val conf = new SparkConf()
      .setAppName(getName)
      .setMaster("local[2]")
    val context: SparkSession = SparkSession.builder().config(conf).getOrCreate()
    val job: T = instantiateJob(context)
    try {
      job.run(configManager)
      context.stop()
    } catch {
      case e: Exception =>
        context.stop()
        throw e
    }
  }
}
