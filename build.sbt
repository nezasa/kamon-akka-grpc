/*
 * =========================================================================================
 * Copyright © 2013-2017 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */

resolvers += Resolver.mavenLocal


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
    crossScalaVersions := Seq("2.11.12", "2.12.8", "2.13.0")),
    javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % "2.0.9" % "test",
    libraryDependencies ++=
      compileScope(kamonCore, kamonAkkaHttp, kamonCommon) ++
      providedScope(kanelaAgent, http25, http2Support, stream25) ++
      testScope(scalatest, slf4jApi, slf4jnop, kamonTestKit))
