lazy val root = project in file(".") dependsOn(RootProject(uri("git://github.com/kamon-io/kamon-sbt-umbrella.git#kamon-2.x")))

addSbtPlugin("com.lightbend.akka.grpc" % "sbt-akka-grpc" % "0.7.2+26-1f67fb52") //due to https://github.com/akka/akka-grpc/pull/702

resolvers += Resolver.bintrayRepo("akka", "maven")
