import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName         = "GH-Game"
  val appVersion      = "0.0.1"

  val appDependencies = Seq(
    // Add your project dependencies here,
  )
  
  val scalaVersion = "2.10"

  val main = PlayProject(
    appName, appVersion, appDependencies, mainLang = SCALA
  ).settings(
    // Add your own project settings here      
  )

}