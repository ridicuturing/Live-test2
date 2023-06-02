package com.github.ridicuturing.guard.manager;

import com.github.ridicuturing.guard.channel.OpenAiChatChannel;
import com.github.ridicuturing.guard.model.AiChannelContext;
import com.github.ridicuturing.guard.model.entity.ChatMessage;
import com.github.ridicuturing.guard.model.dto.AiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


import java.util.List;

@Service
@Slf4j
public class AiManager {

    @Autowired
    private OpenAiChatChannel openAiChatService;


    /*public Mono<ChatResponseDto> ask(String content) {
        return openAiChatService.ask(CollUtil.newLinkedList(
                ChatMessage.builder().role(RoleEnum.USER.getName()).content(content).build()
        ));
    }


    public Mono<ChatResponseDto> ask(String content, String header) {
        return openAiChatService.ask(CollUtil.newLinkedList(
                ChatMessage.builder().role(RoleEnum.SYSTEM.getName()).content(header).build(),
                ChatMessage.builder().role(RoleEnum.USER.getName()).content(content).build()
        ));
    }*/


    public Flux<AiResponseDto> ask(List<ChatMessage> messageDoList, String header) {
        return ask(messageDoList, header, false);
    }


    public Flux<AiResponseDto> ask(List<ChatMessage> messageDoList, String header, boolean stream) {
        return openAiChatService.doAsk(AiChannelContext.builder()
                .systemPrompt(header)
                .model("gpt-3.5-turbo")
                .messageList(messageDoList)
                .stream(stream)
                .build());
    }

    public int requestTokenLimit() {
        return 3500;
    }


}
