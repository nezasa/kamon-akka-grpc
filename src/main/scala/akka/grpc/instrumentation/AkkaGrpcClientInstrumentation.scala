/*
 * =========================================================================================
 * Copyright © 2013-2016 the kamon project <http://kamon.io/>
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

package akka.grpc.instrumentation

import akka.grpc.internal.MetadataImpl
import akka.stream.scaladsl.{Sink, Source}
import kamon.Kamon
import kamon.instrumentation.http.HttpClientInstrumentation
import kamon.util.CallingThreadExecutionContext
import kanela.agent.api.instrumentation.InstrumentationBuilder
import kanela.agent.libs.net.bytebuddy.description.method.MethodDescription
import kanela.agent.libs.net.bytebuddy.matcher.ElementMatchers

import scala.concurrent.Future
import scala.util.{Failure, Success}

class AkkaGrpcClientInstrumentation extends InstrumentationBuilder {

  onSubTypesOf("akka.grpc.internal.ScalaUnaryRequestBuilder")
    .advise(method("invoke").and(ElementMatchers.isPublic[MethodDescription]), classOf[ScalaUnaryRequestBuilderAdvice])
    .advise(method("invokeWithMetadata").and(ElementMatchers.isPublic[MethodDescription]), classOf[ScalaUnaryRequestBuilderAdvice])

  onSubTypesOf("akka.grpc.internal.ScalaServerStreamingRequestBuilder")
    .advise(method("invokeWithMetadata").and(ElementMatchers.isPublic[MethodDescription]), classOf[ScalaServerStreamingRequestBuilderAdvice])
  
  onSubTypesOf("akka.grpc.internal.ScalaClientStreamingRequestBuilder")
    .advise(method("invokeWithMetadata").and(ElementMatchers.isPublic[MethodDescription]), classOf[ScalaUnaryRequestBuilderAdvice])
  
  onSubTypesOf("akka.grpc.internal.ScalaBidirectionalStreamingRequestBuilder")
    .advise(method("invokeWithMetadata").and(ElementMatchers.isPublic[MethodDescription]), classOf[ScalaServerStreamingRequestBuilderAdvice])

  
}

object AkkaGrpcClientInstrumentation {

  Kamon.onReconfigure(_ => rebuildHttpClientInstrumentation(): Unit)

  @volatile private[grpc] var _httpClientInstrumentation: HttpClientInstrumentation = rebuildHttpClientInstrumentation

  private[grpc] def rebuildHttpClientInstrumentation(): HttpClientInstrumentation = {
    val httpClientConfig = Kamon.config().getConfig("kamon.instrumentation.akka.grpc.client")
    _httpClientInstrumentation = HttpClientInstrumentation.from(httpClientConfig, "akka-grpc")
    _httpClientInstrumentation
  }

  def handleResponse(response: Future[_], handler: HttpClientInstrumentation.RequestHandler[MetadataImpl]): Unit = {
    response.onComplete {
      case Success(_) => handler.processResponse(RequestBuilder.toResponse(/*response*/))
      case Failure(t) => handler.span.fail(t).finish()
    }(CallingThreadExecutionContext)
  }
  def handleResponse[A, B](response: Source[A, B], handler: HttpClientInstrumentation.RequestHandler[MetadataImpl]): Source[A, B] = {
    val sink = Sink.onComplete {
      case Success(_) => handler.processResponse(RequestBuilder.toResponse(/*response*/))
      case Failure(t) => handler.span.fail(t).finish()
    }
    response.wireTap(sink)
  }

}
    
      
