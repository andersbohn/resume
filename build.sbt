//import com.typesafe.startscript.StartScriptPlugin

//seq(StartScriptPlugin.startScriptForClassesSettings: _*)

scalaVersion := "2.10.2"

name := "resume"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)     

play.Project.playScalaSettings
