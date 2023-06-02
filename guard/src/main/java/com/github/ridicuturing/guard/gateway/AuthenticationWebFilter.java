package com.github.ridicuturing.guard.gateway;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.WeakCache;
import cn.hutool.core.util.IdUtil;
import com.github.ridicuturing.guard.mapper.ChatUserMapper;
import com.github.ridicuturing.guard.model.entity.ChatUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
public class AuthenticationWebFilter implements WebFilter {

    public static final String COOKIE_SESSION_NAME = "user_session_id";

    private static final WeakCache<Object, ChatUser> userTokenCache = CacheUtil.newWeakCache(3600 * 1000);

    @Lazy
    @Autowired
    private ChatUserMapper chatUserMapper;

    public AuthenticationWebFilter() {
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        MultiValueMap<String, HttpCookie> cookies1 = request.getCookies();
        HttpCookie loginId = cookies1.getFirst(COOKIE_SESSION_NAME);
        String loginSessionId = loginId != null ? loginId.getValue() : null;

        if (StringUtils.hasText(loginSessionId)) {
            return Mono.justOrEmpty(userTokenCache.get(loginSessionId))
                    .switchIfEmpty(chatUserMapper.findOneBySessionId(loginSessionId))
                    .switchIfEmpty(createLoginToken(exchange, loginSessionId))
                    .doOnNext(u -> exchange.getAttributes().put("chatUser", u))
                    .then(chain.filter(exchange));

        }
        return createLoginToken(exchange, IdUtil.fastSimpleUUID())
                .doOnNext(u -> exchange.getAttributes().put("chatUser", u))
                .then(chain.filter(exchange));
    }

    private Mono<ChatUser> createLoginToken(ServerWebExchange exchange, String newToken) {
        return chatUserMapper.save(ChatUser.builder()
                        .userSn(IdUtil.fastSimpleUUID())
                        .sessionId(newToken)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build())
                .doOnNext(e -> exchange.getResponse().addCookie(ResponseCookie.from(COOKIE_SESSION_NAME, newToken).maxAge(Duration.ofDays(3650)).build()))
                .doOnSuccess(c -> userTokenCache.put(newToken, c));
    }

}
