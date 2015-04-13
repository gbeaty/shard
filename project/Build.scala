package sync

import sbt._
import Keys._

import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

object Sync extends Build {

  val appVersion = "0.0.1"
  val scala = "2.11.6"

  val datomic = "com.datomic" % "datomic-free" % "0.9.5153"
  val datomisca = "com.github.dwhjames" %% "datomisca" % "0.7.0"

  val commonResolvers = Seq(
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases"),
    Resolver.url("sbt-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
    "Clojars" at "http://clojars.org/repo",
    Resolver.bintrayRepo("dwhjames", "maven"),
    "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
  )

  scalacOptions in Test ++= Seq("-Yrangepos")

  def project(name: String, settings: Seq[sbt.Def.Setting[_]] = Defaults.defaultSettings) = sbt.Project(
    name,
    base = file(name),
    settings = settings ++ Seq(
      scalaVersion := scala,
      resolvers ++= commonResolvers
    )    
  )

  lazy val core = project("core")

  def subproject(name: String, settings: Seq[sbt.Def.Setting[_]] = Defaults.defaultSettings) =
    project(name, settings).dependsOn(core)

  lazy val client = subproject("client")
  lazy val server = subproject("server").settings(
    libraryDependencies ++= Seq(datomic,datomisca)
  ).dependsOn(client)
  lazy val play = subproject("play").dependsOn(client, server)
  lazy val test = subproject("test").dependsOn(core, client, play).settings(
    libraryDependencies ++= Seq(
      datomic,
      "org.specs2" %% "specs2-core" % "3.4" % "test",
      "org.scalacheck" %% "scalacheck" % "1.12.2" % "test"
    )
  )
  lazy val browser = subproject("browser", ScalaJSPlugin.projectSettings).dependsOn(client).enablePlugins(ScalaJSPlugin)

  override def rootProject = Some(test)
}