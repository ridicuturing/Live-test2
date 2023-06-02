package com.github.ridicuturing.guard.constant.enums;

import cn.hutool.core.map.MapBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class MessageRole {

    @AllArgsConstructor
    public enum LocalRoleEnum {
        USER,
        AI,
        SYSTEM,
        ASSISTANT,
        ;
    }

    @AllArgsConstructor
    @Getter
    public enum OpenAiRoleEnum {
        ASSISTANT("assistant"),
        USER("user"),
        SYSTEM("system"),
        ;
        private final String name;

        private static Map<LocalRoleEnum, OpenAiRoleEnum> M = MapBuilder.create(new HashMap<LocalRoleEnum, OpenAiRoleEnum>())
                .put(LocalRoleEnum.USER, OpenAiRoleEnum.USER)
                .put(LocalRoleEnum.AI, OpenAiRoleEnum.ASSISTANT)
                .put(LocalRoleEnum.SYSTEM, OpenAiRoleEnum.SYSTEM)
                .put(LocalRoleEnum.ASSISTANT, OpenAiRoleEnum.ASSISTANT)
                .build();
        public static OpenAiRoleEnum getByLocalRoleEnum(LocalRoleEnum e) {
            return M.get(e);
        }
    }
}
