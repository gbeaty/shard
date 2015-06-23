package shard

import sbt._
import Keys._

import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

object Shard extends Build {

  val appVersion = "0.0.1"
  val scala = "2.11.6"

  val datomic = "com.datomic" % "datomic-free" % "0.9.5173"
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

  lazy val core = crossProject.in(file("core")).
    settings(
      name := "core",
      version := appVersion,
      scalaVersion := scala,
      resolvers ++= commonResolvers
    ).jvmSettings(
      libraryDependencies ++= Seq(datomic, datomisca, "me.chrons" %% "boopickle" % "1.0.0")
    ).jsSettings(
      libraryDependencies ++= Seq("me.chrons" %%% "boopickle" % "1.0.0")
    )

  lazy val jvm = core.jvm
  lazy val js = core.js

  lazy val test = sbt.Project(
    "test",
    file("test"),
    settings = settings ++ Seq(
      scalaVersion := scala,
      resolvers ++= commonResolvers,
      libraryDependencies ++= Seq(
        datomic,
        "org.specs2" %% "specs2-core" % "3.6" % "test",
        "org.scalacheck" %% "scalacheck" % "1.12.2" % "test"
      )
    )
  ).dependsOn(jvm, js)

  scalaJSStage in Global := FastOptStage

  override def rootProject = Some(test)
}