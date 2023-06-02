package com.github.ridicuturing.guard.mapper;

import com.github.ridicuturing.guard.model.entity.ChatInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ChatInfoMapper extends R2dbcRepository<ChatInfo, Long> {

    Mono<ChatInfo> findLastByChatSn(String chatSn);

    Flux<ChatInfo> findByUserSn(String userId, Pageable pageable);

}