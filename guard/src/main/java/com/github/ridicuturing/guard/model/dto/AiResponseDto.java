package com.github.ridicuturing.guard.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiResponseDto {

    private String role;

    @JsonAlias("text")
    private String content;

    private Integer tokens;

    private String chatSn;

    private boolean stream;


}
