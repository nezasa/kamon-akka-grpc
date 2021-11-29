/*
 * Copyright (C) 2018-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package kamon.testkit

import scala.concurrent.Future
import akka.NotUsed
import akka.grpc.scaladsl.{BytesEntry, Metadata, StringEntry}
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import example.myapp.helloworld.grpc._

class GreeterServiceImpl(implicit mat: Materializer) extends GreeterServicePowerApi {
  import mat.executionContext

  override def sayHello(in: HelloRequest, metadata: Metadata): Future[HelloReply] = {
    println(s"sayHello to ${in.name}")
    Future.successful(HelloReply(s"Hello, ${in.name}"))
  }

  override def itKeepsTalking(in: Source[HelloRequest, NotUsed], metadata: Metadata): Future[HelloReply]  = {
    println(s"sayHello to in stream...")
    in.runWith(Sink.seq).map(elements => HelloReply(s"Hello, ${elements.map(_.name).mkString(", ")}"))
  }

  override def itKeepsReplying(in: HelloRequest, metadata: Metadata): Source[HelloReply, NotUsed] = {
    println(s"sayHello to ${in.name} with stream of chars...")
    Source(s"Hello, ${in.name}".toList).map(character => HelloReply(character.toString))
  }

  override def streamHellos(in: Source[HelloRequest, NotUsed], metadata: Metadata): Source[HelloReply, NotUsed] = {
    println(s"sayHello to stream...")
    in.map(request => HelloReply(s"Hello, ${request.name}"))
  }

  override def replyWithHeaders(in: HeadersRequest, metadata: Metadata): Future[HeadersReply] = {
    println(metadata.asMap)
    Future.successful(HeadersReply(metadata.asMap.mapValues { x =>
      x.head match {
        case StringEntry(value) => value
        case BytesEntry(value) => value.utf8String
      }
    }.toMap))
  }

  override def replyWithHeadersStream(in: HeadersRequest, metadata: Metadata): Source[HeadersReply, NotUsed] = Source({
    println(metadata.asMap)
    metadata.asMap.map { case (k, v) =>
      val value = v.head match {
        case StringEntry(value) => value
        case BytesEntry(value) => value.utf8String
      }
      HeadersReply(Map(k -> value))
    }
  })

  override def waite(in: HelloRequest, metadata: Metadata): Future[HelloReply] = Future {
    Thread.sleep(1000)
    HelloReply()
  }
}
