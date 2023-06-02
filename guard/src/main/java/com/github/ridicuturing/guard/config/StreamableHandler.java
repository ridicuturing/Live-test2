package com.github.ridicuturing.guard.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.HandlerResultHandler;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.result.method.annotation.AbstractMessageWriterResultHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.*;
import java.util.List;

@Component
public class StreamableHandler  extends AbstractMessageWriterResultHandler implements HandlerResultHandler {


    public StreamableHandler(ServerCodecConfigurer serverCodecConfigurer, @Qualifier("webFluxContentTypeResolver") RequestedContentTypeResolver contentTypeResolver, @Qualifier("webFluxAdapterRegistry") ReactiveAdapterRegistry adapterRegistry) {
        super(serverCodecConfigurer.getWriters(), contentTypeResolver, adapterRegistry);
        setOrder(0);
    }

    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface Streamable {

    }


    @Override
    public boolean supports(HandlerResult result) {
        MethodParameter returnType = result.getReturnTypeSource();
        boolean b = returnType.hasMethodAnnotation(Streamable.class);
        return b;
    }

    @Override
    public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
        Object body = result.getReturnValue();
        MethodParameter bodyTypeParameter = result.getReturnTypeSource();
        if(body instanceof Mono) {
            return writeBody(body, bodyTypeParameter, exchange);
        } else if(body instanceof Flux) {
            exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_EVENT_STREAM);
            return writeBody(body, bodyTypeParameter, exchange);
        } else {
            return writeBody(body, bodyTypeParameter, exchange);
        }
    }


}
