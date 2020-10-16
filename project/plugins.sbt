lazy val root = project in file(".") dependsOn(RootProject(uri("git://github.com/kamon-io/kamon-sbt-umbrella.git#kamon-2.x")))

addSbtPlugin("com.lightbend.akka.grpc" % "sbt-akka-grpc" % "0.7.3")

resolvers += Resolver.bintrayRepo("akka", "maven")
