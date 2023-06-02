package com.github.ridicuturing.guard.constant.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatMessageType {
    USER_REQUEST(0),
    AI_WAIT_TO_COMPLETION(10),
    AI_FINISH(11),
    AI_TOKEN_LIMIT(12),
    ;

    private int value;
}
