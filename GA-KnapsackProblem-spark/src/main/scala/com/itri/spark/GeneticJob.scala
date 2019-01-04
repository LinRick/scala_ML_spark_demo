package com.itri.spark

import GeneticAlgorithm.GA._
import GeneticAlgorithm._
import domain.FitnessKnapsackProblem
import domain.generateIndividualBoolean._
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}

/**
  *  Class Created by jmlopez on 01/01/16.
  */
object GeneticJob {

  // case class GAStat (generation: Int, averageFit: Double, totalFit: Double)
  def main(args: Array[String]): Unit = {
    var argv = args // String[]
    if (args.length == 0) {
      argv = Array("/root/GAConfig/GA_config")
    }

    val setup = AppConfig(argv(0))
    println("setup: "+setup)

    // Spark Config
    val sparkConf = new SparkConf().setAppName("Genetic-Algorithm-Over-Spark")//.setMaster("local[*]")
    //val sparkConf = new SparkConf()
    val sc = new SparkContext(sparkConf)

    // Data definition
    val v_array = Array(4500.0, 5700.0, 2250.0, 1100.0, 6700.0)
    val w_array = Array(4.0, 5.0, 2.0, 1.0, 6.0)

    val values  = sc.broadcast(Vectors.dense(v_array).toDense)
    val weights = sc.broadcast(Vectors.dense(w_array).toDense)
    val crhmSize = values.value.size-1 // Data size
    //val values = sc.broadcast(Vectors.dense(Array.fill(setup.chromSize)(math.random*100)).toDense)
    // 生成 chromSize 個 1~100 亂數 array
    //val weights =  sc.broadcast(Vectors.dense(Array.fill(setup.chromSize)(math.random*10)).toDense)
    // 生成 chromSize 個 1~10 亂數 array

    //Problem definition
    val maxW = setup.maxW
    //println("maxW: "+maxW)
    //println("crhmSize: "+crhmSize)
    //Fitness function definition
    val fitnessKSP = new FitnessKnapsackProblem(values, weights, maxW)

    // GA definition
    val sizePopulation = setup.worldSize // 全部群體大小 = numPopulations * sizePop
    val selectionPer = setup.selectionPercentage
    val mutationProb = setup.mutProb
    val numGenerations = setup.numGenerations


    /*
    Inside the class GeneticJob there are two Seq:
    val selections: Selector[SelectionFunction] = new Selector(Seq(new SelectionNaive, new SelectionRandom, new SelectionWrong))
    val mutations: Selector[MutationFunction] = new Selector(Seq(new OnePointMutation, new OnePointMutation, new NoMutation))
    Partition == 0 => will be applied (SelectionNaive && OnePointMutation)
    Partition == 1 => will be applied (SelectionRandom && OnePointMutation)
    Partition == 3 => will be applied (SelectionWrong && NoMutation)
    Partition == 4 => the same than Partition == 0 ...
     */

    // GA 運算元: 選擇 (Selector): 使用隨機遍歷抽樣
    /*val selections = new Selector(Seq(
      SelectionOperators.selectionNaive[Boolean](selectionPer) _,
      SelectionOperators.selectionNaive[Boolean](selectionPer) _,
      SelectionOperators.selectionNaive[Boolean](selectionPer) _
    ))*/

    /*val selections = new Selector(Seq(
      SelectionOperators.selectionNaive[Boolean](selectionPer) _
    ))*/

  val selections = new Selector(Seq(
      SelectionOperators.selectionNaive[Boolean](selectionPer) _,
      SelectionOperators.selectionRandom[Boolean](selectionPer) _,
      SelectionOperators.selectionWrong[Boolean](selectionPer) _
    ))

    // GA 運算元: 突變 (mutations)
    /*val mutations = new Selector(Seq(
      MutationOperators.onePointMutation(mutationProb) _,
      MutationOperators.noMutation _,
      MutationOperators.onePointMutation(mutationProb*20000) _
    ))*/

    /*val mutations = new Selector(Seq(
      MutationOperators.onePointMutation(mutationProb) _
    ))*/

    val mutations = new Selector(Seq(
      MutationOperators.onePointMutation(mutationProb) _,
      MutationOperators.onePointMutation(mutationProb) _,
      MutationOperators.noMutation _
    ))


    // Creation Random Population 初始族群 (initialPopulation) 搭配適應度函數 (Fitness function)
    val populationRDD = sc.parallelize(initialPopulationBoolean(crhmSize, sizePopulation), setup.numPopulations).
      map(ind => ind(fitnessKSP.fitnessFunction))
    println("----------Running GA over Spark---------")

    // selectAndCrossAndMutatePopulation function from GA.scala 包含 交配(100%交配) 與 取代(無保留菁英策略)運算元
    val result = selectAndCrossAndMutatePopulation(
      populationRDD,
      fitnessKSP,
      maxW,
      numGenerations,
      selections,
      mutations)

    //val totalFitness: Option[Double] = result._1.map(indv => indv.fitnessScore).reduce((acc, curr) => if (curr.get > 0) { Some(acc.get + curr.get)} else acc)

    //println("totalFitness: "+result._1.map(indv => indv.fitnessScore).reduce((acc, curr) => if (curr.get > 0) { Some(acc.get + curr.get)} else acc))


    //println("Final results:\n"+result._2.map(ind => (ind.indexPop, ind.fitnessScore.get, ind.chromosome)).collect().mkString(";\n"))
    println("Final Populations results: Feasible solutions:\n"+result._2.map(ind => (ind.indexPop, ind.fitnessScore.get, ind.chromosome)).collect().mkString("\n"))

    //sc.stop()
  }
}

