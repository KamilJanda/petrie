name := "crawler-test"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.21",

  "org.seleniumhq.selenium" % "selenium-server" % "3.141.59",
  "org.seleniumhq.selenium" % "selenium-java" % "3.141.59",
  "com.ning" % "async-http-client" % "1.7.19"
)