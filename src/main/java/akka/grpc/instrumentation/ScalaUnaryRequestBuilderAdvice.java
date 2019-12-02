package akka.grpc.instrumentation;

import akka.grpc.GrpcClientSettings;
import akka.grpc.instrumentation.RequestBuilder;
import akka.grpc.internal.MetadataImpl;
import io.grpc.MethodDescriptor;
import kamon.Kamon;
import kamon.instrumentation.http.HttpClientInstrumentation;
import kamon.instrumentation.http.HttpMessage;
import kamon.trace.Span;
import kanela.agent.libs.net.bytebuddy.asm.Advice;
import scala.concurrent.Future;

public class ScalaUnaryRequestBuilderAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.FieldValue("descriptor") MethodDescriptor<?, ?> descriptor,
                               @Advice.FieldValue("settings") GrpcClientSettings settings,
                               @Advice.FieldValue(value = "headers", readOnly = false) MetadataImpl headers,
                               @Advice.Local("oldHeaders") MetadataImpl oldHeaders,
                               @Advice.Local("span") Span span,
                               @Advice.Local("handler") HttpClientInstrumentation.RequestHandler<MetadataImpl> handler) {
        
        HttpMessage.RequestBuilder<MetadataImpl> requestBuilder = RequestBuilder.toRequestBuilder(descriptor, headers, settings);
        handler = AkkaGrpcClientInstrumentation$.MODULE$._httpClientInstrumentation().createHandler(requestBuilder, Kamon.currentContext());
        
        oldHeaders = headers;
        
        headers = handler.request();
        
        span = handler.span();
        
        span.name(descriptor.getFullMethodName());
    }

    @Advice.OnMethodExit
    public static void onExit(@Advice.Return Future<Object> response,
                              @Advice.FieldValue(value = "headers", readOnly = false) MetadataImpl headers,
                              @Advice.Local("oldHeaders") MetadataImpl oldHeaders,
                              @Advice.Local("span") Span span,
                              @Advice.Local("handler") HttpClientInstrumentation.RequestHandler<MetadataImpl> handler) {

        headers = oldHeaders;
        handler.processResponse(RequestBuilder.toResponse());
    }
}
