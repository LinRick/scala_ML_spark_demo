package com.itri.spark

import scala.io.Source
/**
  * GraphInsight: Class Created by jmlopez on 08/04/16.
  */
/**
  * numGenerations:        演算次數--> 達到該數量則結束演算
  * mutProb:              突變率--> 基因交叉與變異的比例 (mutationChance)
  * selectionPercentage:     選擇率 (用於GA的選擇運算元)
  * numPopulations:       種群數量 代表有幾個最佳解 例如numPopulations=2, 則出現 (0,9050.0);(1,9050.0), 其中 9050.0 代表最佳解的答案(Y值)
  * sizePop:             群體大小 (Population Size) //幾種組合sizePop, 例如sizePop = 10 則在Populations中出現 10 種組合 (List)
  * chromSize:          染色體chromosome的基因數 //可以看成輸入的 sample size (必須一X對一Y)
  */


case class AppConfig(properties: Map[String, String]) extends java.io.Serializable {
  val numGenerations = properties.getOrElse("numGenerations", "1000").toInt
  val mutProb: Float = properties.getOrElse("mutProb", "0.01").toFloat
  val selectionPercentage = properties.getOrElse("selectionPercentage", "0.5").toFloat
  val numPopulations = properties.getOrElse("numPopulations", "8").toInt // Use a value >= numCores in your cluster
  val sizePop = properties.getOrElse("sizePop", "1000").toInt
  val worldSize = numPopulations * sizePop
  val chromSize = properties.getOrElse("chromSize", "300").toInt
  val maxW = properties.getOrElse("maxWeight_problem", "157").toInt

}

/**
  *  This object will help to read a properties file
  */
object AppConfig {

  def apply(file: String): AppConfig = {
    new AppConfig(AppConfig.readProperties(file))
  }

  /**
    * Returns all the properties contained in a given file
    * @param file
    * @return
    */
  def readProperties(file: String): Map[String, String] = {
    var map: Map[String, String] = Map()
    val lines = Source.fromFile(file).getLines()
    for (l <- lines if l.length > 0 && !l.startsWith("#") && l.contains("="))
      map += (l.split("=")(0) -> l.split("=")(1))
    map
  }
}
