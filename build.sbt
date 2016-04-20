name := "kilometer-scala"

version := "0.1"

scalaVersion := "2.10.6"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.twitter" %% "finagle-http" % "6.30.0",
  "joda-time" % "joda-time" % "2.7",
  "org.joda" % "joda-convert" % "1.2",
  "org.json4s" %% "json4s-native" % "3.3.0"
)