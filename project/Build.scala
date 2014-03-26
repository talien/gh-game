import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName         = "GH-Game"
  val appVersion      = "0.0.1"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "securesocial" %% "securesocial" % "2.1.1"
  )

  val main = PlayProject(
    appName, appVersion, appDependencies, mainLang = SCALA
  ).settings(
    // Add your own project settings here
    resolvers += Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)
  )

}