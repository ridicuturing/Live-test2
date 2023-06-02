package com.github.ridicuturing.guard.channel.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Component
@EnableConfigurationProperties(ApiTokenProperties.class)
@Slf4j
public class ApiKeyPoolComponent {


    private final ApiTokenProperties apiTokenProperties;


    private HashMap<String, AtomicInteger> tokenMap = new HashMap<>();

    public ApiKeyPoolComponent(ApiTokenProperties apiTokenProperties) {
        this.apiTokenProperties = apiTokenProperties;
    }

    public String getOpenAiKey() {
        AtomicInteger n = tokenMap.computeIfAbsent("openai", k -> new AtomicInteger());
        String[] openaiKeys = apiTokenProperties.getTokenMap().get("openai");
        String token = openaiKeys[n.getAndIncrement() % openaiKeys.length];
        //log.info("{}", token);
        return token;
    }





}
