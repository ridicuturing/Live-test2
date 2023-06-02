package com.github.ridicuturing.guard.model.http;

import lombok.Data;

@Data
public class ChatRequest {

    private String content;

    private String chatSn;

    private Boolean delayResponse;
}
