scalaVersion := "2.11.6"

libraryDependencies += "io.reactivex" %% "rxscala" % "0.24.0"
libraryDependencies += "org.specs2" %% "specs2-core" % "3.0.1" % "test"

initialCommands in console := """
  import rx.lang.scala._
"""
