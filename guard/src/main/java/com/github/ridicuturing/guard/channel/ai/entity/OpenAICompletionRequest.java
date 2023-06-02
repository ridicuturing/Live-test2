package com.github.ridicuturing.guard.channel.ai.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class OpenAICompletionRequest {

    private String model;

    private String prompt;

    private Integer maxTokens;

    private String temperature;

}
