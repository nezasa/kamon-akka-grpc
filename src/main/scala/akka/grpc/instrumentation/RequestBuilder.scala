package akka.grpc.instrumentation

import akka.grpc.{GrpcClientSettings, GrpcResponseMetadata}
import akka.grpc.internal.MetadataImpl
import akka.stream.scaladsl.Flow
import akka.util.OptionVal
import io.grpc.MethodDescriptor
import kamon.instrumentation.http.HttpMessage

import scala.concurrent.{ExecutionContext, Future}

object RequestBuilder {

  def toResponse(/*grpcResponseMetadata: GrpcResponseMetadata*/): HttpMessage.Response = new HttpMessage.Response {
    override val statusCode: Int = 200
  }
  
  def toRequestBuilder(_descriptor: MethodDescriptor[_, _], _headers: MetadataImpl, _grpcClientSettings: GrpcClientSettings): HttpMessage.RequestBuilder[MetadataImpl] =
    new RequestReader with HttpMessage.RequestBuilder[MetadataImpl] {
      val descriptor: MethodDescriptor[_, _] = _descriptor
      var headers: MetadataImpl = _headers
      val grpcClientSettings: GrpcClientSettings = _grpcClientSettings
      
      override def write(header: String, value: String): Unit =
        headers = headers.addEntry(header, value)

      override def build(): MetadataImpl =
        headers
    }

  /**
   * Bundles together the read parts of the HTTP Request mapping
   */
  private trait RequestReader extends HttpMessage.Request {
    def descriptor: MethodDescriptor[_, _]
    def headers: MetadataImpl
    def grpcClientSettings: GrpcClientSettings

    override def url: String =
      ""

    override def path: String =
      descriptor.getFullMethodName

    override def method: String =
      ""

    override def host: String =
      ""

    override def port: Int =
      grpcClientSettings.defaultPort

    override def read(header: String): Option[String] = {
      ???
    }

    override def readAll(): Map[String, String] = {
      ???
    }
  }
  
}
