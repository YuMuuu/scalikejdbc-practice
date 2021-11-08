
name := "scalikejdbc-practice2"

version := "0.1"

scalaVersion := "2.13.7"

libraryDependencies += "org.scalikejdbc" %% "scalikejdbc" % "4.0.0"
libraryDependencies += "org.scalikejdbc" %% "scalikejdbc-config" % "4.0.0"
libraryDependencies += "org.postgresql" % "postgresql" % "42.3.1" % Runtime
libraryDependencies += "org.typelevel" %% "cats-core" % "2.6.1"