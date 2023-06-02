package com.github.ridicuturing.guard.service;

import cn.hutool.core.util.StrUtil;
import com.github.ridicuturing.guard.common.ActionParser;
import com.github.ridicuturing.guard.common.GroovyActionParserImpl;
import com.github.ridicuturing.guard.constant.enums.RoleEnum;
import com.github.ridicuturing.guard.mapper.ActionMapper;
import com.github.ridicuturing.guard.model.entity.Action;
import com.github.ridicuturing.guard.model.entity.ChatMessage;
import com.github.ridicuturing.guard.mapper.ChatMessageMapper;
import com.github.ridicuturing.guard.manager.AiManager;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


import java.time.LocalDateTime;
import java.util.List;

/**
 * 行动服务
 *
 * @author chenzhihai
 */
@Service
@Slf4j
public class ActionService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ActionMapper actionMapper;

    @Autowired
    private AiManager aiManager;

    @Value("${maxAiCorrecterRetry:5}")
    public int maxAiCorrecterRetry;

    @Autowired
    GroovyActionParserImpl actionParser;

    public void analysis(Long messageId) {
        chatMessageMapper.findById(messageId);
        return;
    }

    public Mono<Action> analysis(ChatMessage chatMessage) {
        String code = actionParser.analysis(chatMessage.getContent());
        if(StrUtil.isBlank(code)) {
            return Mono.empty();
        }
        chatMessage.setActionType("groovy");
        Action action = Action.builder()
                .state(0L)
                .actionContent(code)
                .messageSn(chatMessage.getMessageSn())
                .chatSn(chatMessage.getChatSn())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        return actionMapper.save(action);
    }

    public Mono<List<Action>> command(String aiResponse, ChatMessage chatMessage) {
        return Mono.never();
    }

    public Mono<String> doit(Long messageId) {
        return chatMessageMapper.findById(messageId)
                .flatMap(messageDo -> doit(messageDo, new GroovyActionParserImpl()));
    }

    public Mono<String> doit(ChatMessage messageDo) {
        return doit(messageDo, new GroovyActionParserImpl());
    }

    public Mono<String> doit(ChatMessage messageDo, ActionParser actionParser) {
        try {
            //doBeforeParse
            Object parseResult = actionParser.parse(messageDo.getContent());
            //doBeforeRun
            String r = actionParser.run(parseResult);

            return Mono.just(r);
        } catch (IllegalStateException e) {
            return chatMessageMapper.findByChatSn(messageDo.getChatSn())
                    .collectList()
                    .flatMap(messageDos -> makeItCorrect(messageDos, actionParser, 0));
        }
    }

    public Mono<String> doit(List<ChatMessage> messageDos, ActionParser actionParser) {
        return doit(messageDos.get(messageDos.size() - 2), actionParser);
    }

    private  Mono<String> makeItCorrect(List<ChatMessage> messageDos, ActionParser actionParser, int retryCount) {
        if (retryCount < maxAiCorrecterRetry) {
            return aiManager.ask(messageDos, null)
                    .elementAt(1)
                    .flatMap(chatResponseDto -> {
                        try {
                            //doBeforeParse
                            Object parseResult = actionParser.parse(chatResponseDto.getContent());
                            //doBeforeRun
                            String r = actionParser.run(parseResult);
                            return Mono.just(r);
                        } catch (IllegalStateException e) {
                            messageDos.add(ChatMessage.builder()
                                    .content(chatResponseDto.getContent())
                                    .role(RoleEnum.ASSISTANT.getName())
                                    .build());
                            messageDos.add(ChatMessage.builder()
                                    .content(e.getMessage())
                                    .role(RoleEnum.USER.getName())
                                    .build());
                            return makeItCorrect(messageDos, actionParser, retryCount + 1);
                        }
                    });
        }
        throw new IllegalStateException("out of retry limit for fix groovy content!");
    }

    public static void main(String[] args) {
        String content = "@Grab('org.apache.commons:commons-lang3:3.12.0')\n" +
                "    import org.apache.commons.lang3.StringUtils\n" +
                "    def result = StringUtils.capitalize('hello world')\n" +
                "    println result";
        Script parse = new GroovyShell().parse(content);
        System.out.println(parse.run());
    }
}
