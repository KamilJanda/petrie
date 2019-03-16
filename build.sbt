name := "crawler-test"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= akka ++ macWire ++ selenium ++ others

val akka = Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.21",
)

val others = Seq(
  "com.ning" % "async-http-client" % "1.7.19",
  "org.jsoup" % "jsoup" % "1.8.3"
)

val macWire = Seq(
  "com.softwaremill.macwire" %% "macros" % "2.3.2" % Provided,
  "com.softwaremill.macwire" %% "util" % "2.3.2"
)

val selenium = Seq(
  "org.seleniumhq.selenium" % "selenium-server" % "3.141.59",
  "org.seleniumhq.selenium" % "selenium-java" % "3.141.59",
)