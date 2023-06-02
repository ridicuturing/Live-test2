package com.github.ridicuturing.guard.controller.api;

import com.github.ridicuturing.guard.mapper.ActionMapper;
import com.github.ridicuturing.guard.model.entity.Action;
import com.github.ridicuturing.guard.model.entity.ChatMessage;
import com.github.ridicuturing.guard.model.entity.ChatUser;
import com.github.ridicuturing.guard.service.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;


@RestController
@RequestMapping("/api")
@CrossOrigin
@SessionAttributes("chatUser")
public class ActionController {

    @Autowired
    private ActionService actionService;

    @Autowired
    private ActionMapper actionMapper;


    @PostMapping("command")
    public Object command(@RequestBody String command) {
        return actionService.command(command, null);
    }

    @GetMapping("action/{id}/do")
    public Mono<String> doAction(@PathVariable Long id) {
        return actionService.doit(id);
    }

    @GetMapping("action")
    public Mono get(ServerWebExchange exchange, ChatUser chatUser) {
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        Action param = Action.builder()
                //todo .userId(chatUser.getId())
                .build();
        Optional.ofNullable(queryParams.getFirst("messageId")).ifPresent(e -> param.setMessageSn(e));
        return actionMapper.findAll(Example.of(param)).collectList();
    }
}
