package com.github.ridicuturing.guard.model.dto;

import com.github.ridicuturing.guard.constant.enums.AiModel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChannelConfig {

    String apiKey;

    boolean stream;

    AiModel aiModel;

}
