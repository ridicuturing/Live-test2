package com.github.ridicuturing.guard.controller.api;

import com.github.ridicuturing.guard.channel.ai.entity.OpenAIChatRequest;
import com.github.ridicuturing.guard.mapper.ChatMessageMapper;
import com.github.ridicuturing.guard.mapper.ChatUserMapper;
import com.github.ridicuturing.guard.model.entity.ChatMessage;
import com.github.ridicuturing.guard.model.entity.ChatUser;
import com.github.ridicuturing.guard.model.http.ChatRequest;
import com.github.ridicuturing.guard.service.ChatInfoService;
import com.github.ridicuturing.guard.service.ChatMessageService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.CorePublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
////@PreAuthorize("hasAuthority(T(com.github.ridicuturing.guard.model.SecurityConstant$AuthorityEnum).CHAT.name())")
@SessionAttributes("chatUser")
@RequestMapping("/api")
@CrossOrigin
@Slf4j
public class MessageController {

    @Autowired
    private ChatInfoService chatService;

    @Autowired
    private ChatUserMapper chatUserMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ChatMessageService chatMessageService;


    @PostMapping("message")
    public Mono postMessage(@RequestBody ChatRequest chatRequest, @RequestAttribute ChatUser chatUser, ServerHttpResponse response) {
        boolean delayResponse = Boolean.TRUE.equals(chatRequest.getDelayResponse());
        Mono<ChatMessage> chatMessage = chatMessageService
                .inputUserMessage(chatUser.getUserSn(), chatRequest.getChatSn(), chatRequest.getContent(), delayResponse)
                ;
        if (delayResponse) {
            return chatMessage
                    .doOnError(e -> log.error("", e))
                    .doOnNext(e -> log.info("{}", e))
                    //.onErrorReturn(MessageEvent.builder().content("service error!").build())
                    //.mapNotNull(e->e.getContent())
                    ;
        }
        return chatMessage
                .doOnError(e -> log.error("", e))
                .onErrorReturn(ChatMessage.builder().build())
                .thenReturn(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("接口繁忙"))
                ;
    }

    @Data
    @Builder
    public static class MessageEvent{
        private String content;
    }


    @PostMapping("message-demo")
    public CorePublisher demo(@RequestBody ChatRequest chatRequest, ServerHttpResponse response) {
        boolean stream = Boolean.TRUE.equals(chatRequest.getDelayResponse());
        if (stream) {
            response.getHeaders().setContentType(MediaType.TEXT_EVENT_STREAM);
            return Flux.range(1,10)
                    .map(i -> OpenAIChatRequest.Message.builder().content(i + "").build())
                    .delayElements(Duration.ofSeconds(1))
                    //.mapNotNull(e->e.getContent())
                    ;
        }
        //response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return Mono.just(1)
                .map(i -> OpenAIChatRequest.Message.builder().content(i + "").build())
                ;
    }

    @GetMapping("message/{messageSn}")
    public CorePublisher getMessage(ServerWebExchange exchange, @RequestAttribute ChatUser chatUser, @PathVariable String messageSn) {
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        boolean stream = Boolean.parseBoolean( queryParams.getFirst("stream"));
        boolean regenerate = Boolean.parseBoolean( queryParams.getFirst("regenerate"));
        Flux message = chatMessageService.getMessage(messageSn, stream, regenerate)
                .onErrorResume(e-> Mono.just(ChatMessage.builder().content(((Throwable)e).getMessage()).build()))
                ;
        if (stream) {
            exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_EVENT_STREAM);
            return message
                    .map(m -> MessageEvent.builder().content(((ChatMessage)m).getContent()).build())
                    ;

        }
        return message.last();
    }

    @GetMapping("message")
    public Mono<ResponseEntity<List<ChatMessage>>> getMessages(ServerWebExchange exchange, @RequestAttribute ChatUser chatUser) {
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        ChatMessage param = ChatMessage.builder().userSn(chatUser.getUserSn()).build();
        Optional.ofNullable(queryParams.getFirst("chatSn")).ifPresent(param::setChatSn);
        return chatMessageMapper.findAll(Example.of(param))
                .collectList()
                .map(ResponseEntity::ok);
    }

}
