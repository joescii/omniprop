name := "omniprop"

organization := "com.joescii"

homepage := Some(url("https://github.com/joescii/omniprop"))

version := "0.2"

scalaVersion := "2.10.3"

// Ideally, would support 2.9.1 and up.
// Lowest supportable version is 2.9.3 due to FiniteDurationProperty
// But, can't support 2.9.3 with Lift 2.5.1 due to LiftPropProvider
// crossScalaVersions := Seq("2.10.3")

resolvers ++= Seq(
  "sonatype-snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "sonatype-releases"  at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= {
  Seq(
    "net.liftweb"    %% "lift-webkit" % "2.5.1"  % "optional,compile",
    "org.scalatest"  %% "scalatest"   % "1.9.1"  % "test",
    "org.scalacheck" %% "scalacheck"  % "1.10.1" % "test"
  )
}

scalacOptions <<= scalaVersion map { v: String =>
  val opts = "-deprecation" :: "-unchecked" :: Nil
  if (v.startsWith("2.9.")) opts 
  else opts ++ ("-feature" :: "-language:postfixOps" :: "-language:implicitConversions" :: Nil)
}

publishTo <<= version { _.endsWith("SNAPSHOT") match {
    case true  => Some("snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
    case false => Some("releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
  }
}

credentials += Credentials( file("sonatype.credentials") )

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
         <scm>
            <url>git@github.com:joescii/omniprop.git</url>
            <connection>scm:git:git@github.com:joescii/omniprop.git</connection>
         </scm>
         <developers>
            <developer>
              <id>joescii</id>
              <name>Joe Barnes</name>
              <url>https://github.com/joescii</url>
            </developer>
         </developers>
)

parallelExecution in test := false
 
licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

seq(lsSettings :_*)

(LsKeys.tags in LsKeys.lsync) := Seq("properties", "reflection")

(description in LsKeys.lsync) := "Scala DSL for unifying JVM property libraries in a type-safe manner"

(LsKeys.ghUser in LsKeys.lsync) := Some("joescii")

(LsKeys.ghRepo in LsKeys.lsync) := Some("omniprop")

(LsKeys.ghBranch in LsKeys.lsync) := Some("master")
