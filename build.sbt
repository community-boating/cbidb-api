name := """CBI-DB-API"""

version := "0.1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala, DebianPlugin)

scalaVersion := "2.13.12" // rel 11 sep 2023

maintainer in Linux := "Jonathan Cole <jon@community-boating.org>"

packageSummary in Linux := "API Provider for Community Boating, Inc"

packageDescription := "API Provider for Community Boating, Inc"


//libraryDependencies += jdbc
//libraryDependencies += cache
libraryDependencies += ws
libraryDependencies += filters
libraryDependencies += guice
//libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test

// https://mvnrepository.com/artifact/org.scalactic/scalactic
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.17" // rel 7 sep 2023

// https://mvnrepository.com/artifact/org.scalatest/scalatest
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % Test // rel 7 sep 2023

// https://mvnrepository.com/artifact/org.scalatestplus/junit-4-13
libraryDependencies += "org.scalatestplus" %% "junit-4-13" % "3.2.17.0" % Test // rel 7 sep 2023

// https://mvnrepository.com/artifact/org.scalaj/scalaj-http
libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.4.2" // rel 15 jun 2019

// https://mvnrepository.com/artifact/redis.clients/jedis
libraryDependencies += "redis.clients" % "jedis" % "5.1.0" // rel 21 nov 2023

// https://mvnrepository.com/artifact/com.zaxxer/HikariCP
libraryDependencies += "com.zaxxer" % "HikariCP" % "5.1.0" // rel 4 nov 2023

// https://mvnrepository.com/artifact/io.sentry/sentry
libraryDependencies += "io.sentry" % "sentry" % "6.34.0" // rel 20 nov 2023

// https://mvnrepository.com/artifact/net.sf.barcode4j/barcode4j
libraryDependencies += "net.sf.barcode4j" % "barcode4j" % "2.1"

// https://mvnrepository.com/artifact/mysql/mysql-connector-java
libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.30" // rel 25 jul 2022

// https://mvnrepository.com/artifact/org.apache.commons/commons-csv
libraryDependencies += "org.apache.commons" % "commons-csv" % "1.10.0" // rel 02 feb 2023

// https://mvnrepository.com/artifact/org.apache.pdfbox/fontbox
libraryDependencies += "org.apache.pdfbox" % "fontbox" % "3.0.0" // rel 18 aug 2023

// https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox
libraryDependencies += "org.apache.pdfbox" % "pdfbox" % "3.0.0" // rel 18 aug 2023


/*
scalacOptions ++= Seq(
  "-Xcheckinit"
//  , "-feature"
)
*/ 
