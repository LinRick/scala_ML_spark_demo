
name := "scalaCoreNLP"

version := "0.1"

scalaVersion := "2.11.8"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
assemblyJarName in assembly := "scalaCoreNLP.jar"

libraryDependencies ++= {
  val nlpVersion = "3.7.0"
  Seq(
    "edu.stanford.nlp" % "stanford-corenlp" % nlpVersion,
    "com.google.protobuf" % "protobuf-java" % "3.6.0",
    "edu.stanford.nlp" % "stanford-corenlp" % nlpVersion % "compile" classifier "models"

  )
}