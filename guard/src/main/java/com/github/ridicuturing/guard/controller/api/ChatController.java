package com.github.ridicuturing.guard.controller.api;

import com.github.ridicuturing.guard.mapper.ChatInfoMapper;
import com.github.ridicuturing.guard.model.entity.ChatInfo;
import com.github.ridicuturing.guard.model.entity.ChatUser;
import com.github.ridicuturing.guard.model.http.ChatRequest;
import com.github.ridicuturing.guard.model.http.ErrorResponse;
import com.github.ridicuturing.guard.service.ChatInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;


@RestController
//@PreAuthorize("hasAuthority(T(com.github.ridicuturing.guard.model.SecurityConstant$AuthorityEnum).CHAT.name())")
@SessionAttributes("chatUser")
@RequestMapping("/api")
@CrossOrigin
public class ChatController {

    @Autowired
    private ChatInfoService chatInfoService;
    @Autowired
    private ChatInfoMapper chatInfoMapper;



    @GetMapping(path = {"chat", "chat/{chatSn}"})
    public Mono<ResponseEntity<?>> chats(ServerWebExchange exchange, @PathVariable(name = "chatSn", required = false) String chatSn, @RequestAttribute ChatUser chatUser) {
        if(StringUtils.hasText(chatSn)) {
            return chatInfoService.getChatInfo(chatSn, chatUser)
                    .map(ResponseEntity::ok)
                    ;
        }
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        Integer pageSize =  Optional.ofNullable(queryParams.getOrDefault("pageSize", List.of("10"))).map(e ->e.get(0)).map(Integer::parseInt).orElse(10);
        Integer pageNumber =  Optional.ofNullable(queryParams.getOrDefault("pageNumber", List.of("0"))).map(e ->e.get(0)).map(Integer::parseInt).orElse(0);
        return Mono.just(chatUser.getUserSn())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("getId can't be null")))
                .flatMap(chatUserSn-> chatInfoService.getUserChat(chatUserSn, pageSize, pageNumber))
                .map(ResponseEntity::ok);
    }

    @PostMapping(path = {"chat", "chat/{chatSn}"})
    public Mono<ResponseEntity<?>> chat(@RequestBody ChatInfo body, @PathVariable(name = "chatSn", required = false) String chatSn, @RequestAttribute ChatUser chatUser) {
        return chatInfoService.save(chatSn, chatUser, body)
                .map(ResponseEntity::ok);
    }
}
