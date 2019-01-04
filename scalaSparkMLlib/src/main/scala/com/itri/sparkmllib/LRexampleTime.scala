package com.itri.sparkmllib

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionWithSGD}
import scala.math._

object LRexampleTime {
  def time[R](block: => R): R = {
    val start = System.nanoTime()
    val result = block // call-by-name
    val end = System.nanoTime()
    println("\n\n--------------------------------")
    println("runing past:[" + (end - start)*pow(10,-9) + "s]")
    println("--------------------------------")
    result
  }
  def main(args: Array[String]) {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    time{
      val conf = new SparkConf()
      val sc = new SparkContext(conf)
      val data = sc.textFile("/root/spark-2.3.1-bin-hadoop2.6/data/mllib/ridge-data/very-big-lpsa.data")
      val parsedData = data.map { line =>
        val parts = line.split(',')
        LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
      }.cache()

      // Building the model
      val numIterations = 100
      val stepSize = 0.00000001
      val model = LinearRegressionWithSGD.train(parsedData, numIterations, stepSize)

      // Evaluate model on training examples and compute training error
      val valuesAndPreds = parsedData.map { point =>
        val prediction = model.predict(point.features)
        (point.label, prediction)
      }
      val MSE = valuesAndPreds.map{ case(v, p) => math.pow((v - p), 2) }.mean()
      println(s"training Mean Squared Error $MSE")
    }

    // Save and load model
    //model.save(sc, "target/tmp/scalaLinearRegressionWithSGDModel")
    //val sameModel = LinearRegressionModel.load(sc, "target/tmp/scalaLinearRegressionWithSGDModel")
  }
}
