package akka.grpc.instrumentation;

import akka.grpc.GrpcClientSettings;
import akka.grpc.GrpcResponseMetadata;
import akka.grpc.internal.MetadataImpl;
import akka.stream.scaladsl.Flow;
import akka.stream.scaladsl.Source;
import akka.util.OptionVal;
import io.grpc.MethodDescriptor;
import kamon.Kamon;
import kamon.instrumentation.http.HttpClientInstrumentation;
import kamon.instrumentation.http.HttpMessage;
import kamon.trace.Span;
import kanela.agent.libs.net.bytebuddy.asm.Advice;
import scala.concurrent.Future;

public class ScalaServerStreamingRequestBuilderAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.FieldValue("descriptor") MethodDescriptor<?, ?> descriptor,
                               @Advice.FieldValue("settings") GrpcClientSettings settings,
                               @Advice.FieldValue(value = "headers", readOnly = false) MetadataImpl headers,
                               @Advice.FieldValue(value = "defaultFlow", readOnly = false) Future<OptionVal<Flow<?, ?, Future<GrpcResponseMetadata>>>> defaultFlow,
                               @Advice.Local("oldHeaders") MetadataImpl oldHeaders,
                               @Advice.Local("span") Span span,
                               @Advice.Local("handler") HttpClientInstrumentation.RequestHandler<MetadataImpl> handler) {
        
        HttpMessage.RequestBuilder<MetadataImpl> requestBuilder = akka.grpc.instrumentation.RequestBuilder.toRequestBuilder(descriptor, headers, settings);
        handler = AkkaGrpcClientInstrumentation$.MODULE$._httpClientInstrumentation().createHandler(requestBuilder, Kamon.currentContext());

        oldHeaders = headers;

        headers = handler.request();

        defaultFlow = akka.grpc.instrumentation.RequestBuilder.noneFlow(defaultFlow);  //Very ugly hack so that headers that I set above are included in the flow :(

        span = handler.span();

        span.name(descriptor.getFullMethodName());
    }

    @Advice.OnMethodExit
    public static void onExit(@Advice.Return(readOnly = false) Source<?, ?> response,
                              @Advice.FieldValue(value = "headers", readOnly = false) MetadataImpl headers,
                              @Advice.Local("oldHeaders") MetadataImpl oldHeaders,
                              @Advice.Local("span") Span span,
                              @Advice.Local("handler") HttpClientInstrumentation.RequestHandler<MetadataImpl> handler) {

        headers = oldHeaders;
        response = AkkaGrpcClientInstrumentation.handleResponse(response, handler);
    }
}
