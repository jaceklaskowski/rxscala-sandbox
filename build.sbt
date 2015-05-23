scalaVersion := "2.11.6"

libraryDependencies += "io.reactivex" %% "rxscala" % "0.24.1"
libraryDependencies += "org.specs2" %% "specs2-core" % "3.6" % "test"

initialCommands in console := """
  import rx.lang.scala._
"""
