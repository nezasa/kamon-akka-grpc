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




val kamonCore           = "io.kamon" %% "kamon-core"                    % "2.0.0"
val kamonTestKit        = "io.kamon" %% "kamon-testkit"                 % "2.0.2"
val kamonCommon         = "io.kamon" %% "kamon-instrumentation-common"  % "2.0.0"
val kamonAkkaHttp       = "io.kamon" %% "kamon-akka-http"               % "2.0.0"
val kanelaAgent         = "io.kamon" %  "kanela-agent"                  % "1.0.1"

val http25              = "com.typesafe.akka" %% "akka-http"            % "10.1.10"
val http2Support        = "com.typesafe.akka" %% "akka-http2-support"   % "10.1.10"
val stream25            = "com.typesafe.akka" %% "akka-stream"          % "2.5.24"
val akkaGrpcRuntime     = "com.lightbend.akka.grpc" %% "akka-grpc-runtime" % "0.7.2" 
val grpcStub           = "io.grpc" % "grpc-stub" % "1.24.0" 


lazy val root = (project in file("."))
  .enablePlugins(JavaAgent, AkkaGrpcPlugin)
  .settings(organizationSettings)
  .settings(instrumentationSettings)
  .settings(resolvers += Resolver.bintrayRepo("akka", "maven"))
  .settings(Seq(
    name := "kamon-akka-grpc",
    moduleName := "kamon-akka-grpc",
    bintrayPackage := "kamon-akka-grpc",
    crossScalaVersions := Seq("2.12.8", "2.13.0")),
    javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % "2.0.9" % "test",
    libraryDependencies := libraryDependencies.value.filterNot(m => m.organization == "com.lightbend.akka.grpc" && m.name.startsWith("akka-grpc-runtime")),
    libraryDependencies := libraryDependencies.value.filterNot(m => m.organization == "io.grpc" && m.name.startsWith("grpc-stub")),
    libraryDependencies ++=
      providedScope(akkaGrpcRuntime, grpcStub) ++ //remove when we use a published version of sbt-akka-grpc
      compileScope(kamonCore, kamonAkkaHttp, kamonCommon) ++
        providedScope(kanelaAgent, http25, http2Support, stream25) ++
        testScope(scalatest, slf4jApi, slf4jnop, kamonTestKit),
    bintrayOrganization := Some("nezasadev"),
    bintrayRepository := _root_.bintray.Bintray.defaultMavenRepository,

    akkaGrpcCodeGeneratorSettings in Test += "server_power_apis",
  )
