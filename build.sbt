import Dependencies._

name := "observatory"

version := "1.0"

scalaVersion := "2.12.7"

resolvers += Resolver.sbtPluginRepo("releases")

lazy val observatory = (project in file("."))
  .settings(
    name := "observatory",
    libraryDependencies ++= backendDeps
  )

scalaSource in Compile := { (baseDirectory in Compile)(_ / "src") }.value

scalaSource in Test := { (baseDirectory in Test)(_ / "test") }.value

resourceDirectory in Compile := { (baseDirectory in Compile) (_ / "resources") }.value