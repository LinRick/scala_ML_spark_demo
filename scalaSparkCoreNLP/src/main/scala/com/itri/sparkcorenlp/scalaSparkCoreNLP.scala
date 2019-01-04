package com.itri.sparkcorenlp

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.log4j.{Level,Logger}
import org.apache.spark.sql.functions._

import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types.{StructField, StructType}
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions.udf

object scalaSparkCoreNLP  {

  private val nlpFunctions = functions

  class CoreNLPtest {
    val conf = new SparkConf()
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    val nlpFunctions = functions
    sqlContext.udf.register("ssplit",nlpFunctions.ssplit)
    sqlContext.udf.register("tokenize",nlpFunctions.tokenize)
    sqlContext.udf.register("pos",nlpFunctions.pos)
    sqlContext.udf.register("lemma",nlpFunctions.lemma)
    sqlContext.udf.register("ner",nlpFunctions.ner)
    sqlContext.udf.register("sentiment",nlpFunctions.sentiment)
    sqlContext.udf.register("score",nlpFunctions.score)
  }

  def main(args: Array[String]) {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val processor = new CoreNLPtest

    val documents_df = processor.sqlContext.createDataFrame(Seq(
      (1, "Stanford University is located in California. It is a great university."),
      (2, "Bucharest is located in Romania. It has a great Polytechnic university.")
    )).toDF("doc_id", "text")
    documents_df.show()

    val sentenceSplit_df = documents_df
      .withColumn("sentences",callUDF("ssplit", col("text")))
      .select(col("doc_id"), explode(col("sentences")).as("sen"))
    sentenceSplit_df.show(truncate = false)

    val tokens_df = sentenceSplit_df
      .withColumn("words",callUDF("tokenize", col("sen")))
    tokens_df.show(truncate = false)

    val pos_df = sentenceSplit_df
      .withColumn("pos_tags",callUDF("pos", col("sen")))
    pos_df.show(truncate = false)

    val lemmas_df = sentenceSplit_df
      .withColumn("lemmas",callUDF("lemma", col("sen")))
    lemmas_df.show(truncate = false)

    val ners_df = sentenceSplit_df
      .withColumn("ners",callUDF("ner", col("sen")))
    ners_df.show(truncate = false)

    val sentimentBySentence_df = sentenceSplit_df
      .withColumn("sentiment",callUDF("sentiment", col("sen")))
    sentimentBySentence_df.show(truncate = false)

    val sentimentDocument_df = sentimentBySentence_df
      .groupBy("doc_id")
      .agg(avg(col("sentiment")).as("sentiment_avg"))
      .withColumnRenamed("doc_id", "id")
    sentimentDocument_df.show(truncate = false)

    val result_df = documents_df
      .join(sentimentDocument_df, documents_df.col("doc_id").equalTo(sentimentDocument_df.col("id")), "left_outer")
      .select("id", "text", "sentiment_avg")
      .withColumn("sentiment", callUDF("score", col("sentiment_avg")))
    result_df.show(truncate = false)


  }
}
