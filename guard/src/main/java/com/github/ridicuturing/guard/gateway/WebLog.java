package com.github.ridicuturing.guard.gateway;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class WebLog implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        //ServerHttpRequest request = exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.add(TRACE_ID, traceId)).build();
        log.info("url:{}", request.getURI());
        long startTime = System.currentTimeMillis();
        return chain.filter(exchange.mutate().request(request).build())
                .doOnTerminate(() -> {
                    log.info("url:{} , costTime:{},ip:{} ,headers:{}",
                            request.getURI(),
                            System.currentTimeMillis() - startTime,
                            request.getRemoteAddress(),
                            request.getHeaders());

                });
    }
}