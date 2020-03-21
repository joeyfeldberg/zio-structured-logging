inThisBuild(
  List(
    organization := "dev.zio",
    scalaVersion := "2.13.1"
  )
)

val zioVersion = "1.0.0-RC18-2"
val zioLogging = "0.2.4"

lazy val root = project
  .in(file("."))
  .settings(
    version := "1.0.0",
    name := "zio-structured-logging",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio"          % zioVersion,
      "dev.zio" %% "zio-logging"  % zioLogging,
      "dev.zio" %% "zio-test"     % zioVersion % "test",
      "dev.zio" %% "zio-test-sbt" % zioVersion % "test"
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )

val circeVersion = "0.12.3"

lazy val examples = project
  .in(file("examples"))
  .dependsOn(root)
  .settings(
    skip in publish := true,
    moduleName := "examples",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core"    % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser"  % circeVersion
    )
  )
