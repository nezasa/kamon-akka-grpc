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

package kamon.testkit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import example.myapp.helloworld.grpc.GreeterServiceHandler

import scala.concurrent.{ExecutionContext, Future}

trait TestWebServer {
  def startServer(interface: String, port: Int)(implicit system: ActorSystem): WebServer = {

    implicit val ec: ExecutionContext = system.dispatcher
    implicit val materializer = ActorMaterializer()

    val greeterService = GreeterServiceHandler(
      new GreeterServiceImpl()
    )
    new WebServer(interface, port, "http", Http().bindAndHandleAsync(greeterService, interface, port))
  }

  class WebServer(val interface: String, val port: Int, val protocol: String, bindingFuture: Future[Http.ServerBinding])(implicit ec: ExecutionContext) {
    def shutdown(): Future[_] = {
      bindingFuture.flatMap(binding => binding.unbind())
    }
  }

}

object TestWebServer extends TestWebServer
