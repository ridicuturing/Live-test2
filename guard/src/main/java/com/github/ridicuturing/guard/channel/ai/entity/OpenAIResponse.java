package com.github.ridicuturing.guard.channel.ai.entity;

import lombok.Data;

import java.util.List;

@Data
public class OpenAIResponse {

    private String id;

    private String object;

    private long created;

    private List<Choice> choices;

    private Usage usage;

    @Data
    public static class Choice {
        private Message message;
        private Message delta;
        private String text;
        private int index;
        private String logprobs;
        private String finishReason;
    }

    @Data
    public static class Message {
        private String role;
        private String content;
    }

    @Data
    public static class Usage {
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;
    }
}
