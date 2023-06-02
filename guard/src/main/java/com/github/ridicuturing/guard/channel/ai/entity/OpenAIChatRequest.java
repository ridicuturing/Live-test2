package com.github.ridicuturing.guard.channel.ai.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class OpenAIChatRequest {

    private String model;

    private List<OpenAIChatRequest.Message> messages;

    private Integer maxTokens;

    private Boolean stream;

    private Float temperature = 2.0f;

    @Data
    @Builder
    public static class Message {

        private String role;

        private String content;

        @AllArgsConstructor
        @Getter
        public enum Role {
            ASSISTANT("assistant"),
            USER("user"),
            SYSTEM("system"),
            ;
            private final String name;
        }

    }
}
