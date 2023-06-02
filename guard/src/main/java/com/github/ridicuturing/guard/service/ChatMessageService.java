package com.github.ridicuturing.guard.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.github.ridicuturing.guard.common.ActionParser;
import com.github.ridicuturing.guard.common.GroovyActionParserImpl;
import com.github.ridicuturing.guard.constant.enums.ChatMessageType;
import com.github.ridicuturing.guard.constant.enums.MessageRole;
import com.github.ridicuturing.guard.manager.AiManager;
import com.github.ridicuturing.guard.mapper.ChatInfoMapper;
import com.github.ridicuturing.guard.mapper.ChatMessageMapper;
import com.github.ridicuturing.guard.model.entity.ChatInfo;
import com.github.ridicuturing.guard.model.entity.ChatMessage;
import com.github.ridicuturing.guard.model.entity.ChatUser;
import com.github.ridicuturing.guard.model.http.ChatRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
@Slf4j
public class ChatMessageService {

    @Autowired
    private AiManager aiManager;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ActionService actionService;

    @Autowired
    private ChatInfoMapper chatInfoMapper;

    Map<String, ActionParser> keyActionMap;

    {
        keyActionMap = new HashMap<>();
        keyActionMap.put("do groovy script", SpringUtil.getBean(GroovyActionParserImpl.class));
        keyActionMap.put("do it", SpringUtil.getBean(GroovyActionParserImpl.class));
    }

    private String headerText = "用中文回答用户的提问,尽可能为用户解决问题";

    public Mono<ChatMessage> inputUserMessage(String userSn, String chatSnInput, String content, boolean delayResponse) {
        final String chatSn = chatSnInput == null ? IdUtil.fastSimpleUUID() : chatSnInput;

        ChatMessage userMessage = ChatMessage.builder()
                .messageSn(IdUtil.fastSimpleUUID())
                .chatSn(chatSn)
                .userSn(userSn)
                .type(ChatMessageType.USER_REQUEST.getValue())
                .tokens(3 + content.length())
                .role(MessageRole.LocalRoleEnum.USER.name())
                .content(content)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        ChatMessage replyMessage = ChatMessage.builder()
                .messageSn(IdUtil.fastSimpleUUID())
                .chatSn(chatSn)
                .userSn(userSn)
                .role(MessageRole.LocalRoleEnum.ASSISTANT.name())
                .type(ChatMessageType.AI_WAIT_TO_COMPLETION.getValue())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        if(delayResponse) {
            return Mono.just(userMessage)
                    .flatMap(chatMessageMapper::save)
                    .then(chatMessageMapper.save(replyMessage))
                    ;
        }
        return Mono.just(userMessage)
                .flatMap(chatMessageMapper::save)
                .then(findConversationContext(userMessage.getChatSn()))
                .flatMapMany(allMessages -> aiManager.ask(allMessages, headerText))
                .last()
                .flatMap(aiResponse -> {
                    replyMessage.setContent(aiResponse.getContent());
                    replyMessage.setTokens(aiResponse.getTokens());
                    replyMessage.setType(ChatMessageType.AI_FINISH.getValue());
                    return chatMessageMapper.save(replyMessage);
                })
                //.publishOn(Schedulers.boundedElastic())
                .doOnTerminate(() -> {
                    actionService.analysis(replyMessage).subscribe();
                });

    }

    public Flux<ChatMessage> inputUserMessageOld(String userSn, String chatSnInput, String content, boolean stream) {
        final String chatSn = chatSnInput == null ? IdUtil.fastSimpleUUID() : chatSnInput;

        ChatMessage userMessage = ChatMessage.builder()
                .messageSn(IdUtil.fastSimpleUUID())
                .chatSn(chatSn)
                .userSn(userSn)
                .tokens(3 + content.length())
                .role(MessageRole.LocalRoleEnum.USER.name())
                .content(content)
                .type(ChatMessageType.USER_REQUEST.getValue())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        ChatMessage replyMessage = ChatMessage.builder()
                .messageSn(IdUtil.fastSimpleUUID())
                .chatSn(chatSn)
                .userSn(userSn)
                .type(ChatMessageType.AI_WAIT_TO_COMPLETION.getValue())
                .role(MessageRole.LocalRoleEnum.ASSISTANT.name())
                //.content(aiResponse.getContent())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        StringBuilder replyContent = new StringBuilder();
        AtomicInteger tokens = new AtomicInteger();
        return Mono.just(userMessage)
                .flatMap(chatMessageMapper::save)
                .then(findConversationContext(userMessage.getChatSn()))
                .flatMapMany(allMessages -> aiManager.ask(allMessages, headerText, stream))
                .mapNotNull(aiResponse -> {
                    if (aiResponse.isStream()) {
                        if (aiResponse.getContent() != null) {
                            replyContent.append(aiResponse.getContent());
                            replyMessage.setContent(aiResponse.getContent());

                            tokens.incrementAndGet();
                            replyMessage.setTokens(tokens.get());
                        } else {
                            return null;
                        }
                    } else {
                        replyMessage.setContent(aiResponse.getContent());
                        replyMessage.setTokens(aiResponse.getTokens());
                    }
                    return replyMessage;
                })
                //.publishOn(Schedulers.boundedElastic())
                .doOnTerminate(() -> {
                    replyMessage.setContent(replyContent.toString());
                    chatMessageMapper
                            .save(replyMessage)
                            .publishOn(Schedulers.boundedElastic())
                            .flatMap(e -> actionService.analysis(e))
                            //.flatMap(e -> actionService.analysis(e))
                            .subscribe();
                });

    }

    private Mono<List<ChatMessage>> findConversationContext(String chatSn) {
        return chatMessageMapper.findByChatSnAndRoleOrderByIdDesc(chatSn, MessageRole.LocalRoleEnum.ASSISTANT.name(), Pageable.ofSize(2))
                .collectList()
                .zipWith(chatMessageMapper.findByChatSnAndRoleOrderByIdDesc(chatSn, MessageRole.LocalRoleEnum.USER.name(), Pageable.ofSize(10))
                        .collectList())
                .map(a ->buildRequestMessages(a.getT1(), a.getT2()))
                .doOnNext(e -> log.info("{}", JSONUtil.toJsonStr(e)));

    }

    private Mono<List<ChatMessage>> findConversationContext(String chatSn, String messageSn) {
        return chatMessageMapper.findByChatSnAndRoleOrderByIdDesc(chatSn, MessageRole.LocalRoleEnum.ASSISTANT.name(), Pageable.ofSize(2))
                .filter(c -> !StrUtil.equals(c.getMessageSn(), messageSn))
                .collectList()
                .zipWith(chatMessageMapper.findByChatSnAndRoleOrderByIdDesc(chatSn, MessageRole.LocalRoleEnum.USER.name(), Pageable.ofSize(10))
                        .collectList())
                .map(a ->buildRequestMessages(a.getT1(), a.getT2()))
                .doOnNext(e -> log.info("{}", JSONUtil.toJsonStr(e)));

    }

    private List<ChatMessage> buildRequestMessages(List<ChatMessage> assistant, List<ChatMessage> user) {
        AtomicInteger tokens = new AtomicInteger();
        int requestTokenLimit = aiManager.requestTokenLimit();
        return Stream.of(assistant, user)
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(ChatMessage::getId))
                .filter(c -> Optional.ofNullable(c.getType()).orElse(0) != ChatMessageType.AI_WAIT_TO_COMPLETION.getValue() && tokens.addAndGet(Optional.ofNullable(c.getTokens()).orElse(0)) < requestTokenLimit)
                .toList();
    }

    public Flux getMessage(@NonNull String messageSn, boolean stream, boolean regenerate) {
        Mono<ChatMessage> message = chatMessageMapper.findOne(Example.of(ChatMessage.builder().messageSn(messageSn).build()));
        StringBuilder replyContent = new StringBuilder();
        AtomicInteger tokens = new AtomicInteger();
        return message
                .flux()
                .flatMap(replyMessage -> {
                    if (regenerate || ChatMessageType.AI_WAIT_TO_COMPLETION.getValue() == replyMessage.getType()) {
                        return findConversationContext(replyMessage.getChatSn(), messageSn)
                                .flatMapMany(allMessages -> aiManager.ask(allMessages, headerText, stream))
                                .mapNotNull(aiResponse -> {
                                    if (stream) {
                                        if (aiResponse.getContent() != null) {
                                            replyContent.append(aiResponse.getContent());
                                            replyMessage.setContent(aiResponse.getContent());

                                            tokens.incrementAndGet();
                                            replyMessage.setTokens(tokens.get());
                                        } else {
                                            return null;
                                        }
                                    } else {
                                        replyMessage.setContent(aiResponse.getContent());
                                        replyMessage.setTokens(aiResponse.getTokens());
                                    }
                                    return replyMessage;
                                })
                                .doOnComplete(() -> {
                                    replyMessage.setContent(replyContent.toString());
                                    replyMessage.setType(ChatMessageType.AI_FINISH.getValue());
                                    chatMessageMapper
                                            .save(replyMessage)
                                            .publishOn(Schedulers.boundedElastic())
                                            //.flatMap(e -> actionService.analysis(e))
                                            .subscribe();
                                });
                    }
                    return Mono.just(replyMessage);
                });


    }

}
