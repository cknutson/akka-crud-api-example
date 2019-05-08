name := "akka-crud-api-example"
version := "1.0"
scalaVersion := "2.12.5"

packageName in Universal := name.value
enablePlugins(JavaAppPackaging)

val akkaVersion      = "2.5.11"
val akkaHttpVersion  = "10.1.1"
val slickVersion     = "3.2.3"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-http"   % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "mysql" % "mysql-connector-java" % "5.1.34",
  "ch.qos.logback" % "logback-classic" % "1.1.5",

  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "com.h2database" % "h2" % "1.3.175" % "test"
)
