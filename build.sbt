name := "semver"

organization := "com.github.haydensikh"

scalaVersion := "2.13.1"

libraryDependencies ++= {
  import DependencyVersions._

  Seq(
    "org.scala-lang.modules" %% "scala-parser-combinators" % scalaModuleParsers,
    "org.scalatest"          %% "scalatest"                % scalaTest      % Test,
    "org.scalatestplus"      %% "scalacheck-1-14"          % scalaTestCheck % Test,
  )
}

scalacOptions ++= Seq(
  "-encoding", "utf8", // Option and arguments on same line
  "-Xlint",
)

def unlinted(options: Seq[String]): Seq[String] =
  options.filterNot(_.startsWith("-Xlint"))

val commonSettings = Seq(
  Compile / console      / scalacOptions := unlinted(scalacOptions.value),
  Compile / consoleQuick / scalacOptions := unlinted(scalacOptions.value),
  Test    / console      / scalacOptions := unlinted(scalacOptions.value),
  Test    / consoleQuick / scalacOptions := unlinted(scalacOptions.value),
)
