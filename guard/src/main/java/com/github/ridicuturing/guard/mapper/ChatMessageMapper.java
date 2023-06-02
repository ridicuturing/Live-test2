package com.github.ridicuturing.guard.mapper;

import com.github.ridicuturing.guard.model.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ChatMessageMapper extends R2dbcRepository<ChatMessage, Long> {

    Flux<ChatMessage> findByChatSn(String chatSn);

    Flux<ChatMessage> findTop10ByChatSnOrderByIdDesc(String chatSn);

    Flux<ChatMessage> findTop10ByChatSnAndRoleOrderByIdDesc(String chatSn, String Role);

    Flux<ChatMessage> findByChatSnAndRoleOrderByIdDesc(String chatSn, String Role, Pageable pageable);
}