name := """CBI DB API"""

version := "0.1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala, DebianPlugin)

scalaVersion := "2.12.4"

maintainer in Linux := "Jonathan Cole <jon@community-boating.org>"

packageSummary in Linux := "API Provider for Community Boating, Inc"

packageDescription := "API Provider for Community Boating, Inc"


//libraryDependencies += jdbc
//libraryDependencies += cache
libraryDependencies += ws
libraryDependencies += filters
libraryDependencies += guice
//libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.3.0"

libraryDependencies +="net.debasishg" %% "redisclient" % "3.4"
libraryDependencies +="com.zaxxer" % "HikariCP" % "3.3.1"

/*
scalacOptions ++= Seq(
  "-Xcheckinit"
//  , "-feature"
)
*/
