package com.github.ridicuturing.guard.mapper;

import com.github.ridicuturing.guard.model.entity.ChatUser;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ChatUserMapper extends R2dbcRepository<ChatUser, Long> {

    Mono<ChatUser> findOneBySessionId(String sessionId);

}