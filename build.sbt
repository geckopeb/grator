name := """grator"""
//Reboot Numero 2.
//..
version := "0.1.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  cache,
  ws,
  specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.35"

libraryDependencies += "com.typesafe.play" %% "play-slick" % "1.0.0"

libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "1.0.0"

libraryDependencies += "commons-io" % "commons-io" % "2.4"
