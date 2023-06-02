package com.github.ridicuturing.guard.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AiModel {
    /**
     * gpt-3.5-turbo also named chatGPT
     */
    CHAT_GPT("gpt-3.5-turbo", "OpenAiGpt35Turbo", Organization.OPENAI, ModelType.TEXT)//, OpenAiGpt35Turbo.class),
    ;

    private final String name;

    private final String serviceName;

    private final Organization organization;

    private final ModelType modelType;

    //private final Class<? extends AiService> beanClass;
}