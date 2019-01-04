# scala_ML_spark_demo project
- Scala exercise over Spark

## Description
- Four projects
- 
    **Including program:**

    | program name | program description |
    | ------ | ------ |
    |GA-KnapsackProblem-spark| GA run over spark only for KnapsackProblem|
    |GAConfig|GA configuration for GA-KnapsackProblem-spark|
    |scalaCoreNLP| CoreNLP run some APIs without spark|
    |scalaSparkCoreNLP| CoreNLP run some APIs over spark|
    |scalaSparkMLlib| SparkMLlib API of Linear regression example|


### In OptunitySparkTest
> **Note:** 
keras exercise with tensorflow
```sh
$ cd "the special project" 
$ sbt clean assembly
# example: scalaSparkMLlib project
$ ./spark-submit --master local[*] \
--class com.itri.sparkmllib.LRexample \
scalasparkmllib.jar
```
