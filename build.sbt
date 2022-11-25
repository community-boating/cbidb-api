name := """CBI-DB-API"""

version := "0.1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala, DebianPlugin)

scalaVersion := "2.12.11"

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

libraryDependencies += "io.sentry" % "sentry" % "1.7.27"

libraryDependencies += "net.sf.barcode4j" % "barcode4j" % "2.1"

// https://mvnrepository.com/artifact/mysql/mysql-connector-java
libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.30"
// https://mvnrepository.com/artifact/com.auth0/java-jwt
libraryDependencies += "com.auth0" % "java-jwt" % "4.2.1"


/*
scalacOptions ++= Seq(
  "-Xcheckinit"
//  , "-feature"
)
*/ 
