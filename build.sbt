name := """silhouette-rest-seed"""

version := "2.0-RC1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  cache,
  ws,
  "com.mohiva" %% "play-silhouette" % "2.0-RC1",
  "com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.0"
)

