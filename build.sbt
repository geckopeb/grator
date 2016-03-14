name := """grator"""
//Reboot Numero 3.
//..
version := "0.2.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  ws,
  specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.38"

libraryDependencies += "com.typesafe.play" %% "play-slick" % "2.0.0"

libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0"

libraryDependencies += "com.typesafe.slick" %% "slick" % "3.1.1"

libraryDependencies += "commons-io" % "commons-io" % "2.4"
