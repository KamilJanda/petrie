import sbt.Keys._
import sbt.{Resolver, _}

object Dependencies {

  val akka = Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.5.21"
  )

  val others = Seq(
    "com.ning" % "async-http-client" % "1.7.19",
    "org.jsoup" % "jsoup" % "1.8.3",
    "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test,
    "io.scalaland" %% "chimney" % "0.3.1"
  )

  val time = Seq(
    "com.github.tototoshi" %% "slick-joda-mapper" % "2.4.0",
  "joda-time" % "joda-time" % "2.9.9",
    "org.joda" % "joda-convert" % "1.9.2"
  )

  val guice = Seq(
    "javax.inject" % "javax.inject" % "1",
    "com.google.inject" % "guice" % "4.1.0"
  )

  val play = Seq(
    "com.typesafe.play" %% "play-ahc-ws" % "2.7.0" % Test,
    "com.typesafe.play" %% "play" % "2.7.0",
    "com.typesafe.slick" %% "slick" % "3.3.0" exclude("com.zaxxer", "HikariCP-java6"),
    "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0" exclude("com.zaxxer", "HikariCP-java6")
  )

  val selenium = Seq(
    "org.seleniumhq.selenium" % "selenium-server" % "3.141.59",
    "org.seleniumhq.selenium" % "selenium-java" % "3.141.59"
  )

  val macWire = Seq(
    "com.softwaremill.macwire" %% "macros" % "2.3.2" % Provided,
    "com.softwaremill.macwire" %% "util" % "2.3.2"
  )

  val database = Seq(
    "org.postgresql" % "postgresql" % "42.2.5"
  )

  val all = akka ++ selenium ++ macWire ++ others ++ play ++ guice ++ database ++ time
}
