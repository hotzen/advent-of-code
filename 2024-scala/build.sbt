lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "aoc",
      scalaVersion := "3.5.2"
    )),
    name := "2024-scala"
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test
libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"