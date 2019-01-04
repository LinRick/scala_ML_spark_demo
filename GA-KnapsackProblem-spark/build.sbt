name := "GA-spark-ex"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "spray repo" at "http://repo.spray.io"

val sparkVersion = "2.3.1"

libraryDependencies ++= Seq(
  "org.apache.spark"  %% "spark-core"    % sparkVersion % "provided",
  "org.apache.spark"  %% "spark-graphx"  % sparkVersion % "provided",
  "org.apache.spark"  %% "spark-mllib"   % sparkVersion % "provided",
  "org.apache.spark"  %% "spark-sql"     % sparkVersion % "provided"
)

// set the main class for packaging the main jar
mainClass in (Compile, packageBin) := Some("GeneticJob")


assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
assemblyJarName in assembly := "gaSparkex.jar"