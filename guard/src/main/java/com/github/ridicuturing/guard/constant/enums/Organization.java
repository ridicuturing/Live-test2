package com.github.ridicuturing.guard.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Organization {
    /**
     * openai.com
     */
    OPENAI("openai"),
    ;

    private final String name;
}