package com.github.ridicuturing.guard.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.github.ridicuturing.guard.common.exception.BussinessException;
import com.github.ridicuturing.guard.mapper.ChatInfoMapper;
import com.github.ridicuturing.guard.model.entity.ChatInfo;
import com.github.ridicuturing.guard.model.entity.ChatUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ChatInfoService {

    @Autowired
    private ChatInfoMapper chatInfoMapper;

    /*public Mono<ChatInfo> newChat(Long userId, String systemPrompt) {
        LocalDateTime now = LocalDateTime.now();
        return chatInfoMapper.save(ChatInfo.builder()
                .chatSn(IdUtil.fastSimpleUUID())
                .systemPrompt(systemPrompt)
                .userSn(userId)
                .createTime(now)
                .updateTime(now)
                .build());
    }*/

    public Mono<Page<ChatInfo>> getUserChat(String chatUserId, int pageSize, int pageNumber) {
        Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
        Example<ChatInfo> example = Example.of(ChatInfo.builder().userSn(chatUserId).build());
        return chatInfoMapper.count(example)
                .flatMap(count ->
                        chatInfoMapper.findByUserSn(chatUserId, pageable)
                                .collectList()
                                .map(u -> new PageImpl<ChatInfo>(u, pageable, count))
                );
    }

    public Mono<ChatInfo> save(String chatSn, ChatUser chatUser, ChatInfo chatInfoRequest) {
        if (StringUtils.hasText(chatSn)) {
            return getChatInfo(chatSn, chatUser)
                    .switchIfEmpty(Mono.error(new BussinessException(HttpStatus.NOT_FOUND, chatSn)))
                    .map(chatInfo -> {
                        Optional.ofNullable(chatInfoRequest.getChatName()).ifPresent(chatInfo::setChatName);
                        Optional.ofNullable(chatInfoRequest.getSystemPrompt()).ifPresent(chatInfo::setSystemPrompt);
                        return chatInfo;
                    })
                    .flatMap(chatInfoMapper::save);
        } else {
            ChatInfo newChatInfo = ChatInfo.builder()
                    .chatSn(IdUtil.fastSimpleUUID())
                    .userSn(chatUser.getUserSn())
                    .chatName(Optional.ofNullable(chatInfoRequest.getChatName()).orElse("new chat"))
                    .systemPrompt(chatInfoRequest.getSystemPrompt())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            return chatInfoMapper.save(newChatInfo);
        }
    }

    public Mono<ChatInfo> getChatInfo(String chatSn, ChatUser chatUser) {
        return chatInfoMapper.findOne(Example.of(ChatInfo.builder().chatSn(chatSn).userSn(chatUser.getUserSn()).build()));
    }
}
