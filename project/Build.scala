import sbt._
import Keys._
import play.Project

object ApplicationBuild extends Build {

  val appName         = "GH-Game"
  val appVersion      = "0.0.1"
  val scalaVersion    = "2.10.0"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "ws.securesocial" %% "securesocial" % "2.1.3"
  )

  val main = play.Project(
    appName, appVersion, appDependencies
  ).settings(
    // Add your own project settings here
    // resolvers += Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

    resolvers += Resolver.sonatypeRepo("releases")
  )

}
