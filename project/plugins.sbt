lazy val root = project in file(".") dependsOn(RootProject(uri("https://github.com/kamon-io/kamon-sbt-umbrella.git#kamon-2.x")))

addSbtPlugin("com.lightbend.akka.grpc" % "sbt-akka-grpc" % "2.1.6")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.1.2")
