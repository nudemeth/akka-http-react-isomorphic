val akkaVersion = "2.5.4"

organization := "com.nudemeth"
name := "akka-http-react-isomorphic"
version := "0.1.0-SNAPSHOT"
scalaVersion := "2.12.4"
resolvers += Classpaths.typesafeReleases
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor"  % akkaVersion
)

enablePlugins(SbtTwirl)