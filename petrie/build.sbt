name := """petrie"""

version := "1.0"

scalaVersion := "2.12.8"

crossScalaVersions := Seq("2.11.12", "2.12.8")

def playScalaModule(name: String): sbt.Project = {
  module(name).enablePlugins(play.sbt.PlayScala)
}

def module(name: String): sbt.Project = {
  sbt.Project(
    id = name,
    base = file("modules") / name
  ).settings(Common.projectSettings)
}

lazy val common = playScalaModule("common")

lazy val core = playScalaModule("core")
  .dependsOn(scraping, common)

lazy val scraping = module("scraping")

lazy val petrie = (project in file("."))
  .enablePlugins(PlayScala)
  .aggregate(core, scraping, common)
  .dependsOn(core, scraping, common)
  .settings(libraryDependencies ++= Dependencies.all)


// Automatic database migration available in testing
fork in Test := true

libraryDependencies += guice
libraryDependencies += evolutions
