name := "CommonLib"

version := "0.1"

organization := "com.thebrains"

scalaVersion := "2.11.12"

libraryDependencies += "org.rogach" %% "scallop" % "3.1.2"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.0"

libraryDependencies += "com.databricks" %% "spark-csv" % "1.5.0"

libraryDependencies += "org.elasticsearch" % "elasticsearch" % "2.3.2"

libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-core" % "6.2.3"

libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-http" % "6.2.3"