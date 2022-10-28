Kamon akka-grpc
--------------------

*Kamon Akka GRPC* module provides bytecode instrumentation for clients to perform automatic `Context` propagation on your behalf.

Note that server side support is already possible with akka-http

## Releasing and Publishing to Sonatype

### Pre-requisites

Check the instructions https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html, 
specifically the "step 1: PGP Signatures" to create your own PGP signature 
and "step 3: Credentials" to configure sonatype credentials

> **PGP Signature**
> The artifacts need to be signed to be uploaded to Maven, 
> but the signatures are not validated when artifacts are downloaded.
> This mean you can use a signature associated with your personal nezasa email

### Releasing and publishing to Sonatype staging repos

To make a new release and publish to sonatype run `sbt release` locally
and follow the instructions

```
export GPG_TTY=$(tty)
$ sbt release
[info] welcome to sbt 1.7.2 (Homebrew Java 17.0.4.1)
[info] loading settings for project root from plugins.sbt ...
[info] loading settings for project kamon-sbt-umbrella-build from plugins.sbt ...
[info] loading project definition from /Users/joao/.sbt/1.0/staging/f280af28cdd6c5b27c14/kamon-sbt-umbrella/project
[info] loading settings for project kamon-sbt-umbrella from build.sbt,version.sbt ...
[info] loading project definition from /Users/joao/git/nezasa/kamon-akka-grpc/project
[info] loading settings for project root from build.sbt,version.sbt ...
[info] set current project to kamon-akka-grpc (in build file:/Users/joao/git/nezasa/kamon-akka-grpc/)
[info] Starting release process off commit: 78eb20d1ff49e89821d4c90003fe9baeadd17da9
[info] Checking remote [origin] ...
[info] Setting scala version to 2.12.17
[info] set current project to kamon-akka-grpc (in build file:/Users/joao/git/nezasa/kamon-akka-grpc/)
[info] Setting scala version to 2.13.10
[info] set current project to kamon-akka-grpc (in build file:/Users/joao/git/nezasa/kamon-akka-grpc/)
[info] Setting scala version to 2.12.16
[info] set current project to kamon-akka-grpc (in build file:/Users/joao/git/nezasa/kamon-akka-grpc/)
Release version [0.2.1] :
Next version [0.2.2-SNAPSHOT] :
...
```

If all goes well the artifacts should have been uploaded to sonatype staging 
repositories, and then we just need to promote the release to Maven central.

### Promote the release to Maven central

Go to https://oss.sonatype.org/#welcome and close the staging repository and promote the 
release to central, by hitting “Close” button, then “Release” button.


