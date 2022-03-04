name := "Scala Playground"

version := "1.0"

scalaVersion := "2.13.8"
crossScalaVersions := Seq("2.11.12", "2.12.15", "2.13.8")

resolvers ++= Seq(
  "BBC Repository" at "https://artifactory.dev.bbc.co.uk/artifactory/int-bbc-releases",
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
  "Local Maven Repo" at Path.userHome.asFile.toURI + "/.m2/repository"
)

libraryDependencies += "bbc.shared" %% "alertingappender" % "1.1.0"
