name := "TapsServices"

version := "0.1"

scalaVersion := "2.13.10"
val akkaVersion = "2.6.16"
val akkaHttpVersion = "10.2.4"  // Adjust this version according to compatibility

fork := true

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "ch.qos.logback" % "logback-classic" % "1.4.8",
  "mysql" % "mysql-connector-java" % "8.0.33",
  "com.typesafe.slick" %% "slick" % "3.4.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.4.1",
  "com.siftscience" % "sift-java" % "3.2.0",
  "com.github.takezoe" %% "solr-scala-client" % "0.0.27",
  "org.postgresql" % "postgresql" % "42.6.0",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-xml" % "2.15.2",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.15.2",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.15.2",
  "org.scala-lang.modules" %% "scala-xml" % "1.3.0",
  "net.liftweb" %% "lift-json" % "3.4.3",
  "io.circe" %% "circe-core" % "0.14.6",
  "io.circe" %% "circe-generic" % "0.14.6"
)
