import Dependencies._

ThisBuild / organization      := "dk.bohnjespersen.anders"
ThisBuild / version           := "0.0.1"
ThisBuild / scalaVersion      := "3.7.4"
ThisBuild / fork              := true
Compile / fork                := true
ThisBuild / scalacOptions     := optionsOnOrElse("2.13", "2.12")("-Ywarn-unused")("").value
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / scalafixDependencies ++= List("com.github.liancheng" %% "organize-imports" % "0.6.0")
Compile / javaOptions += "-Dfile.encoding=UTF-8"

def settingsApp = Seq(
  name                      := "resume",
  Compile / run / mainClass := Option("dk.bohnjespersen.anders.resume.MainApp"),
  testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
  libraryDependencies ++= Seq(
    zioHttp,
    zioTest,
    zioTestSBT,
    zioTestMagnolia,
  ),
)

def settingsDocker = Seq(
  Docker / version := version.value,
  dockerBaseImage  := "eclipse-temurin:20.0.1_9-jre",
)

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(settingsApp)
  .settings(settingsDocker)

addCommandAlias("fmt", "scalafmt; Test / scalafmt; sFix;")
addCommandAlias("fmtCheck", "scalafmtCheck; Test / scalafmtCheck; sFixCheck")
addCommandAlias("sFix", "scalafix OrganizeImports; Test / scalafix OrganizeImports")
addCommandAlias(
  "sFixCheck",
  "scalafix --check OrganizeImports; Test / scalafix --check OrganizeImports",
)
