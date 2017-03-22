name := """play-scala"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

// you can also add multiple repositories at the same time
resolvers ++= Seq("snapshots"     at "http://oss.sonatype.org/content/repositories/snapshots",
  "staging"       at "http://oss.sonatype.org/content/repositories/staging",
  "releases"      at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= {
  val liftVersion = "2.6" // Put the current/latest lift version here
  Seq(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-wizard" % liftVersion % "compile->default")
}

libraryDependencies ++= Seq(
//  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "org.postgresql" % "postgresql" % "9.4.1212",
  "com.h2database" % "h2" % "1.4.191",
  "com.typesafe.play" %% "play-slick" % "2.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.2"
)



//fork in run := true