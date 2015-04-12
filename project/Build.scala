package sync

import sbt._
import Keys._

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

  def project(name: String) = sbt.Project(
    name,
    base = file(name),
    settings = Defaults.defaultSettings ++ Seq(
      scalaVersion := scala,
      resolvers ++= commonResolvers
    )    
  )

  lazy val core = project("core")

  def subproject(name: String) = project(name).dependsOn(core)

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

  override def rootProject = Some(test)
}