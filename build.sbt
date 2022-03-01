name         := "resume"
organization := "dk.andersbohn"

version := "2.0-SNAPSHOT"

scalaVersion := "2.13.6"

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

import Dependencies.Version

scalacOptions ++= Seq("-Xfatal-warnings")

libraryDependencies ++= Seq(
  "dev.zio" %% "zio"                 % Version.zio,
  "dev.zio" %% "zio-test"            % Version.zio % Test,
  "dev.zio" %% "zio-test-sbt"        % Version.zio % Test,
  "dev.zio" %% "zio-json"            % Version.zioJson,
  "dev.zio" %% "zio-http"            % Version.zioHttp,
  "dev.zio" %% "zio-config"          % Version.zioConfig,
  "dev.zio" %% "zio-config-typesafe" % Version.zioConfig,
  "dev.zio" %% "zio-config-magnolia" % Version.zioConfig
)
scalacOptions ++= Seq("-Ymacro-annotations", "-Xfatal-warnings", "-deprecation")
scalaVersion := "2.13.6"
testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

lazy val root = (project in file("."))

addCommandAlias("fmt", "; scalafmtSbt ; scalafmtAll ; Test / scalafmtAll ")
addCommandAlias("chk", "scalafmtSbtCheck ; scalafmtCheck ; Test / scalafmtCheck ")
