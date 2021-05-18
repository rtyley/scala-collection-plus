

lazy val baseSettings = Seq(
  scalaVersion := "2.13.6",
  organization := "com.madgag",
  licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  publishTo := sonatypePublishToBundle.value,
  scmInfo := Some(ScmInfo(
    url("https://github.com/rtyley/scala-collection-plus"),
    "scm:git:git@github.com:rtyley/scala-collection-plus.git"
  )),
  scalacOptions ++= Seq("-deprecation", "-unchecked")
)

name := "scala-collection-plus-root"

description := "A few odds and ends to replace mapViews"

ThisBuild / scalaVersion := "2.13.6"

lazy val collectionPlus = project.in(file("collection-plus")).settings(
  baseSettings,
  name := "scala-collection-plus",
  crossScalaVersions := Seq(scalaVersion.value, "3.0.0"),
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test
)

lazy val docs = project.in(file("collection-plus-docs")) // important: it must not be docs/
  .dependsOn(collectionPlus)
  .enablePlugins(MdocPlugin)

import ReleaseTransformations._

lazy val collectionPlusRoot = (project in file("."))
  .aggregate(
    collectionPlus
  )
  .settings(baseSettings).settings(
  publishArtifact := false,
  publish := {},
  publishLocal := {},
  releaseCrossBuild := true, // true if you cross-build the project for multiple Scala versions
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    // For non cross-build projects, use releaseStepCommand("publishSigned")
    releaseStepCommandAndRemaining("+publishSigned"),
    releaseStepCommand("sonatypeBundleRelease"),
    setNextVersion,
    commitNextVersion,
    pushChanges
  )
)
