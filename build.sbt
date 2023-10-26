name := "cryptoutils"

organization := "com.henricook"

version := "1.5.3"

isSnapshot := version.value.endsWith("SNAPSHOT")

scalaVersion := "2.13.12"

val Scala212 = "2.12.18"
val Scala213 = "2.13.12"
val Scala3 = "3.3.1"

crossScalaVersions := Seq(Scala212, Scala213, Scala3)

resolvers += "softprops-maven" at "https://dl.bintray.com/content/softprops/maven"

addCommandAlias("c", "compile")
addCommandAlias("cc", ";clean;compile")
addCommandAlias("rc", ";reload;compile")
addCommandAlias("rcc", ";reload;clean;compile")
addCommandAlias("t", "test")
addCommandAlias("tq", "testQuick")

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.15.0",
  "org.bouncycastle" % "bcprov-jdk15on" % "1.63" % "provided",
  "org.bouncycastle" % "bcpkix-jdk15on" % "1.63" % "provided",
  "org.bouncycastle" % "bctls-jdk15on" % "1.63" % "provided",
  "com.typesafe" % "config" % "1.3.4",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)

ThisBuild / organization := "com.henricook"
ThisBuild / organizationName := "Henri Cook"
ThisBuild / organizationHomepage := Some(url("https://github.com/henricook"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/henricook/cryptoutils"),
    "scm:git@github.com:henricook/cryptoutils.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "henricook",
    name  = "Henri Cook",
    email = "github@henricook.com",
    url   = url("https://www.henricook.com")
  )
)

ThisBuild / description := "Cryptoutils for Scala 2.12 and 2.13 - Forked from Karasiq"
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/henricook/cryptoutils"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true
