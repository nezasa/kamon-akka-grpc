val organizationSettings = Seq(
  organization     := "com.github.nezasa",
  organizationName := "nezasa",
  organizationHomepage := Some(url("https://github.com/nezasa/")),

  scmInfo := Some(
    ScmInfo(
      url("https://github.com/nezasa/kamon-akka-grpc/"),
      "scm:git@github.com:nezasa/kamon-akka-grpc.git"
    ),
  ),
  developers := List(
    Developer(
      id    = "nezasadev",
      name  = "Nezasa Devs",
      email = "dev@nezasa.com",
      url   = url("http://nezasa.com")
    )
  ),
  homepage := Some(url("https://github.com/nezasa/kamon-akka-grpc")),
  licenses += ("Apache-2.0", url("https://opensource.org/licenses/Apache-2.0")),
  pomExtra := scala.xml.NodeSeq.Empty,
)




val kamonTestKit        = "io.kamon" %% "kamon-testkit"                 % "2.1.12"
val kamonCommon         = "io.kamon" %% "kamon-instrumentation-common"  % "2.0.0"
val kamonAkkaHttp       = "io.kamon" %% "kamon-akka-http"               % "2.1.12"
val kanelaAgent         = "io.kamon" %  "kanela-agent"                  % "1.0.1"

val http25              = "com.typesafe.akka" %% "akka-http"            % "10.1.10"
val http2Support        = "com.typesafe.akka" %% "akka-http2-support"   % "10.1.10"
val stream25            = "com.typesafe.akka" %% "akka-stream"          % "2.5.24"
val akkaGrpcRuntime     = "com.lightbend.akka.grpc" %% "akka-grpc-runtime" % "2.1.6"


lazy val root = (project in file("."))
  .settings(organizationSettings)
  .settings(
    name := "kamon-akka-grpc",
    moduleName := "kamon-akka-grpc",
    crossScalaVersions := Seq("2.12.17", "2.13.10"),
    libraryDependencies ++=
      providedScope(akkaGrpcRuntime) ++
      compileScope(kamonAkkaHttp) ++
        providedScope(kanelaAgent, http25, http2Support, stream25),
    releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    releaseCrossBuild := true,
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.

lazy val e2eTest = (project in file("e2eTest"))
  .enablePlugins(JavaAgent, AkkaGrpcPlugin)
  .settings(instrumentationSettings)
  .settings(
    publish / skip := true,
    crossScalaVersions := Seq("2.12.17", "2.13.10"),
    libraryDependencies ++= testScope(scalatest, slf4jApi, slf4jnop, kamonTestKit),
    Test / akkaGrpcCodeGeneratorSettings += "server_power_apis",
  )
  .dependsOn(root)