ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

lazy val root = (project in file("."))
  .settings(
    name := "file-encryption"
  )
libraryDependencies ++= Seq("org.connectbot" % "jbcrypt" % "1.0.2", "org.connectbot" % "jbcrypt" % "1.0.2")