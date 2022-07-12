import ReleaseTransformations.*
import sbtversionpolicy.withsbtrelease.ReleaseVersion

name := "scala-collection-plus-root"

description := "A few odds and ends to replace mapViews"

ThisBuild / scalaVersion := "2.13.16"
ThisBuild / crossScalaVersions := Seq(scalaVersion.value, "3.3.4")

lazy val collectionPlus = project.in(file("collection-plus")).settings(
  name := "scala-collection-plus",
  organization := "com.madgag",
  licenses := Seq(License.Apache2),
  scalacOptions ++= Seq("-deprecation", "-unchecked", "-release:11"),
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  Test/testOptions += Tests.Argument(
    TestFrameworks.ScalaTest,
    "-u", s"test-results/scala-${scalaVersion.value}"
  )
)

lazy val docs = project.in(file("collection-plus-docs")) // important: it must not be docs/
  .dependsOn(collectionPlus)
  .enablePlugins(MdocPlugin)

lazy val collectionPlusRoot = (project in file(".")).aggregate(collectionPlus).settings(
  publish / skip := true,
  releaseVersion := ReleaseVersion.fromAggregatedAssessedCompatibilityWithLatestRelease().value,
  releaseCrossBuild := true, // true if you cross-build the project for multiple Scala versions
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    setNextVersion,
    commitNextVersion
  )
)
