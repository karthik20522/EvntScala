organization := "com.kufli"

name := "EvntScala"

version := "1.0"

scalaVersion := "2.11.0"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/",
  "Gamlor-Repo" at "https://github.com/gamlerhart/gamlor-mvn/raw/master/snapshots"
)

libraryDependencies ++= {
  val akkaV = "2.3.2"
  val sprayVersion = "1.2.1"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV,        
    "io.spray" % "spray-client" % sprayVersion,    
    "io.spray" % "spray-util" % sprayVersion,        
    "org.specs2" %% "specs2" % "2.3.12" % "test",
    "org.json4s" %% "json4s-native" % "3.2.9",
    "org.json4s" %% "json4s-jackson" % "3.2.9",
    "com.github.nscala-time" %% "nscala-time" % "1.0.0",
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
    "com.rabbitmq" % "amqp-client" % "3.3.1",
    "com.github.mauricio" % "mysql-async_2.10" % "0.2.13"
  )
}