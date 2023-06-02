package com.github.ridicuturing.guard.channel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.github.ridicuturing.guard.model.dto.AiResponseDto;
import com.github.ridicuturing.guard.channel.ai.AiService;
import com.github.ridicuturing.guard.channel.ai.entity.OpenAIResponse;
import com.github.ridicuturing.guard.channel.ai.entity.OpenAICompletionRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class OpenAiCompletionChannel extends AbstractOpenAiChannel {


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static {
        OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public String getUrl() {
        return "https://api.openai.com/v1/completions";
    }

    public Mono<AiResponseDto> ask(String prompt) {
        return doAsk(prompt,  "text-davinci-003");
    }

    public Mono<AiResponseDto> ask(String prompt, String header) {
        return ask(header + prompt, "text-davinci-003");
    }

    public Mono<AiResponseDto> ask(String prompt, String header, String modelName) {
        return ask(header + prompt, modelName);
    }

    @SneakyThrows
    public Mono<AiResponseDto> doAsk(String prompt, String modelName) {
        OpenAICompletionRequest request = getRequestBody(prompt, modelName);
        String body = OBJECT_MAPPER.writeValueAsString(request);
        log.info("ai request body: {}", body);
        return WebClient.create(getUrl())
                .post()
                .uri(getUrl())
                .headers(headers -> headers.setBearerAuth(getToken()))
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .onStatus(HttpStatus::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                        .log("ai http error, Response body: ")
                        .flatMap(responseBody -> Mono.error(new RuntimeException("ai http error"))))
                .bodyToMono(String.class)
                .doOnNext(s -> log.info("ai response:{}", s))
                .map(s -> {
                    try {
                        return OBJECT_MAPPER.readValue(s, OpenAIResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(s -> AiResponseDto.builder().content(s.getChoices().get(0).getText()).build())
                ;
    }

    private OpenAICompletionRequest getRequestBody(String prompt, String modelName) {
        OpenAICompletionRequest request = new OpenAICompletionRequest();
        request.setModel(modelName);
        request.setPrompt(prompt);
        request.setMaxTokens(getTokenLimit());
        return request;
    }

    private Integer getTokenLimit() {
        return 3000;
    }

}
