package com.itri.sparkmllib

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.log4j.{Level,Logger}

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LinearRegressionModel
import org.apache.spark.mllib.regression.LinearRegressionWithSGD

object LRexample {
    def main(args: Array[String]) {

      Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
      Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

      val conf = new SparkConf()
      val sc = new SparkContext(conf)
      val data = sc.textFile("/usr/lib/spark-2.3.0-bin-hadoop2.7/data/mllib/ridge-data/lpsa.data")
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


    // Save and load model
    //model.save(sc, "target/tmp/scalaLinearRegressionWithSGDModel")
    //val sameModel = LinearRegressionModel.load(sc, "target/tmp/scalaLinearRegressionWithSGDModel")
  }
}
