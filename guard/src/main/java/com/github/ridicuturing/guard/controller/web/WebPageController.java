package com.github.ridicuturing.guard.controller.web;

import com.github.ridicuturing.guard.config.StreamableHandler;
import com.github.ridicuturing.guard.controller.api.MessageController;
import com.github.ridicuturing.guard.model.entity.ChatMessage;
import com.github.ridicuturing.guard.model.entity.ChatUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.CorePublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Controller
public class WebPageController {

    @Autowired
    MessageController messageController;



    @GetMapping("chatroom")
    public Mono<String> chatroom(Model model, ServerWebExchange exchange, @SessionAttribute ChatUser chatUser){
        return Mono.just("chatroom");
    }

    @GetMapping("")
    public Rendering index(){
        return Rendering.redirectTo("/index.html").build();
    }



    /*@GetMapping("chat/{chatId}")
    public Mono<String> chatRoom(@PathVariable String ChatId,  Model model, ServerWebExchange exchange, @SessionAttribute ChatUser chatUser){
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        queryParams.add("chatId", ChatId);
        Mono<ResponseEntity<List<ChatMessage>>> messages = messageController.getMessages(exchange, chatUser);
        return messages.map(ms -> model.addAttribute("messages",ms.getBody()))
                .thenReturn("chat");
    }*/

    @GetMapping("hello")
    public String hello(Model model){
        model.addAttribute("hello","hello welcome");
        return "hello";
    }



    @GetMapping("12/{stream}")
    @ResponseBody
    public CorePublisher s4(@PathVariable boolean stream, ServerHttpResponse response) {
        if(stream) {
            return Flux.just(ChatMessage.builder().messageSn("2").build()).repeat(10).delayElements(Duration.ofSeconds(1));
        }
        return Mono.just(ChatMessage.builder().messageSn("2").build());
    }
}
