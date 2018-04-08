package com.thebrains.utils

import java.nio.file.{Path, Paths}

import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

import scala.reflect.ClassTag
import scala.reflect.io.File
import scala.reflect.runtime.universe._

object IoData {
  def readCsv[T <: Product : ClassTag : TypeTag](
    path: String,
    delimiter: String = ","
  )(implicit spark: SparkSession): Dataset[T] = {
    import spark.implicits._

    spark
      .sqlContext
      .read
      .format("com.databricks.spark.csv")
      .option("header", "true")
      .option("delimiter", delimiter)
      .option("inferSchema", "true")
      .load(path)
      .as[T]
  }

  def writeCsv(
    data: DataFrame,
    path: String,
    delimiter: String = ","
  ): Unit = {
    data
      .write
      .format("com.databricks.spark.csv")
      .option("header", "true")
      .option("delimiter", delimiter)
      .save(path)
  }

  def getResourcePath(filename: String): String = {
    File(System.getProperty("user.dir") + s"/src/main/resources/$filename")
      .toURL
      .getPath
  }

  def getTargetPath(filename: String): String = {
    val currentTargetDir: Path = Paths.get(getClass.getClassLoader.getResource("").toURI)
    val newFolderString = currentTargetDir.toString() + "/output"

    val directory: File = File(newFolderString)
    directory.createDirectory(force = true, failIfExists = false)


    File(directory.toURL.getPath + filename)
      .toURL
      .getPath
  }
}
