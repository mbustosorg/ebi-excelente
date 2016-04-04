import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

packageArchetype.java_application

lazy val commonSettings = Seq(
   organization := "org.bustos",
   version := "0.1.0",
   scalaVersion := "2.11.4"
)

lazy val mainProject = (project in file("."))
    .settings(name := "ebi-excelente")
    .settings(commonSettings: _*)
    .settings(libraryDependencies ++= projectLibraries)
    .settings(resolvers += "Spray" at "http://repo.spray.io")

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

val slf4jV = "1.7.6"
val sprayV = "1.3.2"
val akkaV = "2.3.6"

val projectLibraries = Seq(
    "io.spray"                %% "spray-can"       % sprayV,
    "io.spray"                %% "spray-routing"   % sprayV,
    "io.spray"                %% "spray-testkit"   % sprayV  % "test",
    "io.spray"                %% "spray-json"      % "1.3.1",
    "com.typesafe.akka"       %% "akka-actor"      % akkaV,
    "com.typesafe.akka"       %% "akka-testkit"    % akkaV   % "test",
    "com.typesafe.akka"       %% "akka-slf4j"      % akkaV,
    "org.slf4j"               %  "slf4j-api"       % "1.7.10",
    "ch.qos.logback"          %  "logback-classic" % "1.1.3",
    "com.typesafe.slick"      %% "slick"           % "2.1.0",
    "org.scalatest"           %% "scalatest"       % "2.1.6",
    "org.specs2"              %% "specs2-core"     % "2.3.11" % "test",
    "mysql"                   %  "mysql-connector-java" % "latest.release",
    "joda-time"               %  "joda-time"       % "2.7",
    "org.joda"                %  "joda-convert"    % "1.2"
)

Revolver.settings