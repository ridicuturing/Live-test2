package com.github.ridicuturing.guard.model;

import com.github.ridicuturing.guard.channel.ai.entity.OpenAIResponse;
import com.github.ridicuturing.guard.model.entity.ChatMessage;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AiChannelContext {

    private String systemPrompt;

    private List<ChatMessage> messageList;

    private boolean stream;

    private float temperature;

    private String model;

    private OpenAIResponse openAIResponse;

}
