import sbt._

object Dependencies {

  val akka = Seq(
    "com.typesafe.akka" %% "akka-actor"        % "2.5.21",
    "com.typesafe.akka" %% "akka-stream-typed" % "2.5.21"
  )

  val others = Seq(
    "com.ning"               % "async-http-client"   % "1.7.19",
    "org.jsoup"              % "jsoup"               % "1.8.3",
    "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test,
    "io.scalaland"           %% "chimney"            % "0.3.1",
    "us.codecraft"           % "xsoup"               % "0.3.1",
    "org.webjars"            % "swagger-ui"          % "2.2.0"
  )

  val time = Seq(
    "com.github.tototoshi" %% "slick-joda-mapper" % "2.4.0",
    "joda-time"            % "joda-time"          % "2.9.9",
    "org.joda"             % "joda-convert"       % "1.9.2"
  )

  val guice = Seq(
    "javax.inject"      % "javax.inject" % "1",
    "com.google.inject" % "guice"        % "4.1.0"
  )

  val play = Seq(
    "com.typesafe.play"  %% "play-ahc-ws"           % "2.7.1" % Test,
    "com.typesafe.play"  %% "play"                  % "2.7.1",
    "com.typesafe.play"  %% "play-json"             % "2.7.1",
    "com.typesafe.slick" %% "slick"                 % "3.3.0" exclude ("com.zaxxer", "HikariCP-java6"),
    "com.typesafe.play"  %% "play-slick-evolutions" % "3.0.0" exclude ("com.zaxxer", "HikariCP-java6")
  )

  val selenium = Seq(
    "org.seleniumhq.selenium" % "selenium-server" % "3.141.59",
    "org.seleniumhq.selenium" % "selenium-java"   % "3.141.59"
  )

  val macWire = Seq(
    "com.softwaremill.macwire" %% "macros" % "2.3.2" % Provided,
    "com.softwaremill.macwire" %% "util"   % "2.3.2"
  )

  val database = Seq(
    "org.postgresql" % "postgresql" % "42.2.5"
  )

  val cats = Seq(
    "org.typelevel" %% "cats-effect" % "1.2.0",
    "org.typelevel" %% "cats-core"   % "1.6.0"
  )

  val test = Seq(
    "org.mockito" %% "mockito-scala" % "1.7.1" % Test
  )

  val all = akka ++ selenium ++ macWire ++ others ++ play ++ guice ++ database ++ time ++ cats ++ test
}
