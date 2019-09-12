name := "cryptoutils"

organization := "com.henricook"

version := "1.5.1"

isSnapshot := version.value.endsWith("SNAPSHOT")

scalaVersion := "2.13.0"

crossScalaVersions := Seq("2.11.12", "2.12.4", "2.13.0")

resolvers += "softprops-maven" at "https://dl.bintray.com/content/softprops/maven"

addCommandAlias("c", "compile")
addCommandAlias("cc", ";clean;compile")
addCommandAlias("rc", ";reload;compile")
addCommandAlias("rcc", ";reload;clean;compile")
addCommandAlias("t", "test")
addCommandAlias("tq", "testQuick")

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.5",
  "org.bouncycastle" % "bcprov-jdk15on" % "1.58" % "provided",
  "org.bouncycastle" % "bcpkix-jdk15on" % "1.58" % "provided",
  "com.typesafe" % "config" % "1.3.1",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ ⇒ false }

licenses := Seq("The MIT License" → url("http://opensource.org/licenses/MIT"))

homepage := Some(url(s"https://github.com/henricook/${name.value}"))

pomExtra := <scm>
  <url>git@github.com:henricook/{name.value}.git</url>
  <connection>scm:git:git@github.com:henricook/{name.value}.git</connection>
</scm>
  <developers>
    <developer>
      <id>henricook</id>
      <name>Henri Cook</name>
      <url>https://github.com/henricook</url>
    </developer>
    <developer>
      <id>karasiq</id>
      <name>Piston Karasiq</name>
      <url>https://github.com/Karasiq</url>
    </developer>
  </developers>