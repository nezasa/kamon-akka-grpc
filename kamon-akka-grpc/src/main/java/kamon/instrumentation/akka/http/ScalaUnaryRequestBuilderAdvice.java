package kamon.instrumentation.akka.http;

import io.grpc.MethodDescriptor;
import kamon.Kamon;
import kamon.context.Storage;
import kamon.instrumentation.akka.grpc.AkkaGrpcClientInstrumentation;
import kamon.trace.Span;
import kanela.agent.libs.net.bytebuddy.asm.Advice;
import scala.concurrent.Future;

public class ScalaUnaryRequestBuilderAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.FieldValue("descriptor") MethodDescriptor<?, ?> descriptor,
                               @Advice.Local("span") Span span,
                               @Advice.Local("scope") Storage.Scope scope) {

        String operationName = descriptor.getFullMethodName();
        
        span = Kamon.clientSpanBuilder(operationName, "akka-grpc").start();

        scope = Kamon.storeContext(Kamon.currentContext().withEntry(Span.Key(), span));
    }

    @Advice.OnMethodExit
    public static void onExit(@Advice.Return Future<Object> response,
                              @Advice.Local("span") Span span,
                              @Advice.Local("scope") Storage.Scope scope) {

        AkkaGrpcClientInstrumentation.handleResponse(response, span);
        scope.close();
    }
}
