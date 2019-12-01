ThisBuild / organization     := "com.github.nezasa"
ThisBuild / organizationName := "nezasa"
ThisBuild / organizationHomepage := Some(url("https://github.com/nezasa/"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/nezasa/kamon-akka-grpc/"),
    "scm:git@github.com:nezasa/kamon-akka-grpc.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "nezasadev",
    name  = "Nezasa Devs",
    email = "dev@nezasa.com",
    url   = url("http://nezasa.com")
  )
)


ThisBuild / homepage := Some(url("https://github.com/nezasa/kamon-akka-grpc"))
ThisBuild / licenses += ("Apache-2.0", url("https://opensource.org/licenses/Apache-2.0"))


val kamonCore           = "io.kamon" %% "kamon-core"                    % "2.0.0"
val kamonTestKit        = "io.kamon" %% "kamon-testkit"                 % "2.0.2"
val kamonCommon         = "io.kamon" %% "kamon-instrumentation-common"  % "2.0.0"
val kamonAkkaHttp       = "io.kamon" %% "kamon-akka-http"               % "2.0.0"
val kanelaAgent         = "io.kamon" %  "kanela-agent"                  % "1.0.1"

val http25              = "com.typesafe.akka" %% "akka-http"            % "10.1.10"
val http2Support        = "com.typesafe.akka" %% "akka-http2-support"   % "10.1.10"
val stream25            = "com.typesafe.akka" %% "akka-stream"          % "2.5.24"


lazy val root = (project in file("."))
  .settings(noPublishing: _*)
  .settings(
    name := "kamon-akka-grpc",
    crossScalaVersions := Nil
  ).aggregate(kamonAkkaHttp25)

lazy val kamonAkkaHttp25 = Project("kamon-akka-grpc", file("kamon-akka-grpc"))
  .enablePlugins(JavaAgent, AkkaGrpcPlugin)
  .settings(instrumentationSettings)
  .settings(resolvers += Resolver.bintrayRepo("akka", "maven"))
  .settings(Seq(
    name := "kamon-akka-grpc",
    moduleName := "kamon-akka-grpc",
    bintrayPackage := "kamon-akka-grpc",
    crossScalaVersions := Seq("2.12.8", "2.13.0")),
    javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % "2.0.9" % "test",
    libraryDependencies ++=
      compileScope(kamonCore, kamonAkkaHttp, kamonCommon) ++
      providedScope(kanelaAgent, http25, http2Support, stream25) ++
      testScope(scalatest, slf4jApi, slf4jnop, kamonTestKit),
    bintrayOrganization := Some("nezasadev"),
    bintrayRepository := _root_.bintray.Bintray.defaultMavenRepository
  
  )
