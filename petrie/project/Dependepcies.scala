import sbt.Keys._
import sbt.{Resolver, _}

object Dependencies {

  val playVersion = play.core.PlayVersion.current

  val akka = Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.5.21",
  )

  val others = Seq(
    "javax.inject" % "javax.inject" % "1",
    "joda-time" % "joda-time" % "2.9.9",
    "org.joda" % "joda-convert" % "1.9.2",
    "com.google.inject" % "guice" % "4.1.0",

    "com.ning" % "async-http-client" % "1.7.19",
    "org.jsoup" % "jsoup" % "1.8.3",
    "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test,
    "com.typesafe.play" %% "play-ahc-ws" % playVersion % Test,
    "com.typesafe.play" %% "play" % "2.7.0",
    "com.typesafe.slick" %% "slick" % "3.3.0"
  )

  val selenium = Seq(
    "org.seleniumhq.selenium" % "selenium-server" % "3.141.59",
    "org.seleniumhq.selenium" % "selenium-java" % "3.141.59"
  )

  val macWire = Seq(
    "com.softwaremill.macwire" %% "macros" % "2.3.2" % Provided,
    "com.softwaremill.macwire" %% "util" % "2.3.2"
  )

  val all = akka ++ selenium ++ macWire ++ others
}
