package com.github.ridicuturing.guard.channel.common;

import cn.hutool.core.collection.CollUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.Map;

@ConfigurationProperties("apikey")
@Getter
@Setter
public class ApiTokenProperties {
    private Map<String, String[]> tokenMap;

    @PostConstruct
    public void validate() {
        if (CollUtil.isEmpty(tokenMap)) {
            throw new RuntimeException("properties : {apikey.token-map} is missing!");
        }
    }
}