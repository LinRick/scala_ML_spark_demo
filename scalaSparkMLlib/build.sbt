name := "scalaSparkMLlib"

version := "0.1"

scalaVersion := "2.11.8"
libraryDependencies ++= {
  val sparkVersion = "2.3.1"
  Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion % "provided" withSources(),
    "org.apache.spark" %% "spark-mllib" % sparkVersion % "provided" withSources()
  )
}
libraryDependencies += "log4j" % "log4j" % "1.2.14"


assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
assemblyJarName in assembly := "scalaSparkMLlib.jar"