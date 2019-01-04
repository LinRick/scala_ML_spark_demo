package GeneticAlgorithm

import GeneticAlgorithm.MutationOperators.MutationOperator
import GeneticAlgorithm.SelectionOperators.SelectionOperator
import domain.{Fitness, Individual}
import org.apache.spark.mllib.linalg.DenseVector
import org.apache.spark.rdd.RDD

object GA{

  /**
    *
    * @param population : Original populations to be evolved
    * @param fitness : Fitness Function to measure each Individual
    * @param maxWeight : Max weight to carry in the Knapsack problem
    * @param numGen : Max num of generations
    * @param selectionSelector : Selections to be applied to each population in each generation
    * @param mutationSelector : Mutations to be applied to each population in each generation
    * @return The final population once the stop condition have been satisfied
    */

  def selectAndCrossAndMutatePopulation[T](population: RDD[Individual[T]],
                                           fitness: Fitness,
                                           maxWeight: Double,
                                           numGen: Int,
                                           selectionSelector: Selector[SelectionOperator[T]],
                                           mutationSelector: Selector[MutationOperator])={
    /**
      * This cross function will create two children from parentA and parentB
      * 交配
      * Note : A near future improvement will be the mutation function passed like an argument like the fitness function
      *
      * @param parentA : Individual
      * @param parentB : Individual
      * @return A pair of Individuals product of the crossed between parentA and parentB
      * @todo A near future improvement: mutation function passed like an argument
      */
    def cross(parentA: Individual[T],
              parentB: Individual[T],
              index: Int):(Individual[T], Individual[T])  = {
      val chrSize = parentA.chromosome.size
      val crossPoint = scala.util.Random.nextInt(chrSize)
      /*
            單點交配: 隨機選擇一個交配點, 將兩個基因字串自該交配點以後所有位元進行互換
            此程式交配率設定為100%, 也就是一定會交配.
            通常設定為70%, 通常以亂數值介於0~1之間來決定是否交配,  若是得到的亂數值小於交配率則進行交配, 否之則不交配
       */

      // We'll need the chromosome of each parent to create the new individuals
      val chrmA: Array[Double] = parentA.chromosome.toDense.values.slice(0,crossPoint)++parentB.chromosome.toDense.values.slice(crossPoint,chrSize)
      val chrmB: Array[Double] = parentB.chromosome.toDense.values.slice(0,crossPoint)++parentA.chromosome.toDense.values.slice(crossPoint,chrSize)

      //println("mutationSelector_index = "+index)

      // Mutation.
      val chrmAMutated = mutationSelector(index)(chrmA)
      val chrmBMutated = mutationSelector(index)(chrmB)

      //println("chrmAMutated = "+chrmAMutated)
      //println("chrmBMutated = "+chrmBMutated)


      // Execute of the crossover and creation of the two new individuals
      ( new Individual[T](new DenseVector(chrmAMutated), Some(0.toDouble)),
        new Individual[T](new DenseVector(chrmBMutated), Some(0.toDouble)))
    }

    /**
      * The selection function will select the parents to be crossover to build a new generation.
      *
      * The amount of parents that will be selected is calculated like a percentage of the total size in our population
      *
      * @param index : Population Id
      * @param iter : Population
      * @return
      */
    def selection(index: Int, iter: Iterator[Individual[T]])= {
      var iter2 = iter
      // Selection and replacement must be done "numGen" times

      //println(" selection_index = "+index)

      for (i <- 0 to numGen) {
        println("numGenTH = "+i)
        // Ordering the population by fitnessScore and storing in each Individual the population index (for statistical purposes)
        val currentSelectionOrdered: List[Individual[T]] = iter2.toList.map(
          ind => {Individual[T](ind.chromosome, ind.fitnessScore, bestInd = false, index)}
          )
          .sortBy(x => x.fitnessScore).reverse
        // Calculate the popSize and then the number of Individuals to be selected and to be Crossed
        val initialPopSize = currentSelectionOrdered.size
        val selectionF = selectionSelector(index)
        // Calculate the next Generation
        val springs = selectionF(currentSelectionOrdered).                                //  (1) Selecting the N parents that will create the next childhood
          sliding(2, 2).                                                                  //  (2) Sliding is the trick here: List(0,1,2,3).sliding(2,2).ToList = List(List(0, 1), List(2, 3))
          map(
          l => l match {
            case List(parent_A, parent_B) =>
              val spring = cross(parent_A, parent_B, index)                               //  (3) Now that we have the parents separated in Lists, we can crossover
              List(spring._1(fitness.fitnessFunction).setPop(index), spring._2(fitness.fitnessFunction).setPop(index)) // (4) Remember to fitness the children!!!
            case List(p) =>
              List(p)
          }
        ).
          toList.flatMap(x => x)                                                          // (5) we are interested in a plain List, not in a List of Lists => flatmap(x => x)
        // I've chosen a type of replacement that select the best individuals from the append of: oldPopulation + newPopulation
        val selectedIndividuals = (springs ++ currentSelectionOrdered).sortBy(x => x.fitnessScore).reverse.take(initialPopSize)

        //println("selectedIndividuals.tail="+selectedIndividuals.tail)
        //println("initialPopSize="+initialPopSize)
        //println("selectedIndividuals.head.setBest(true)="+selectedIndividuals.head.setBest(true))

        iter2 = (List(selectedIndividuals.head.setBest(true)) ++ selectedIndividuals.tail).iterator

      }
      /*while (iter2.hasNext)
        println("NewIter2="+iter2.next())*/

      iter2
    }

    // mapPartitionsWithIndex allows us to treat each partition like an entire population
    val populationRDD = population.mapPartitionsWithIndex(selection, preservesPartitioning = true)
    //println("populationRDD.collect\n"+populationRDD.collect())

    val bestIndvs = populationRDD.filter(ind => ind.bestInd)
    //println("bestIndvs="+bestIndvs.collect())

    (bestIndvs,bestIndvs)
  }
}
