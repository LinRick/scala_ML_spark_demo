name := "scalaSparkCoreNLP"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies += "log4j" % "log4j" % "1.2.14"
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
assemblyJarName in assembly := "scalaSparkCoreNLP.jar"

libraryDependencies ++= {
  val sparkVersion = "2.3.1"
  Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion % "provided" withSources(),
    "org.apache.spark" %% "spark-sql" % sparkVersion % "provided" withSources(),
    "org.apache.spark" %% "spark-mllib" % sparkVersion % "provided" withSources()
  )
}

libraryDependencies ++= {
  val nlpVersion = "3.7.0"
  Seq(
    "edu.stanford.nlp" % "stanford-corenlp" % nlpVersion,
    "com.google.protobuf" % "protobuf-java" % "3.6.0",
    "edu.stanford.nlp" % "stanford-corenlp" % nlpVersion % "compile" classifier "models"

  )
}

initialize := {
  val _ = initialize.value
  val required = VersionNumber("1.8")
  val current = VersionNumber(sys.props("java.specification.version"))
  assert(VersionNumber.Strict.isCompatible(current, required), s"Java $required required.")
}

// change the value below to change the directory where your zip artifact will be created
credentials += Credentials(Path.userHome / ".ivy2" / ".sbtcredentials")


// https://mvnrepository.com/artifact/databricks/spark-corenlp
//libraryDependencies += "databricks" % "spark-corenlp" % "0.2.0-s_2.11"
//resolvers += "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven"
//libraryDependencies += "databricks" % "spark-corenlp" % "0.2.0-s_2.10"

