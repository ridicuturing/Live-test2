package com.github.ridicuturing.guard.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ModelType {
    /**
     * only for text
     */
    TEXT("text"),
    ;

    private final String name;
}