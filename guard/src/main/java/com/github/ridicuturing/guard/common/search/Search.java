package com.github.ridicuturing.guard.common.search;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class Search {

    public static Mono<String> search(String terminal, String url) {
        return WebClient
                .create(url)
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .map(body-> processSearchResult(terminal, body))
                ;
    }

    private static String processSearchResult(String terminal, String body) {
        return body;
    }

    public static void main(String[] args) {

    }
}
