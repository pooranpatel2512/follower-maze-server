name := "follower-maze-server"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies := Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.12",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.12",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)
