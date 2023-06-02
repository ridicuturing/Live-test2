package com.github.ridicuturing.guard.constant.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoleEnum {
    ASSISTANT("assistant"),
    USER("user"),
    SYSTEM("system"),
    ;
    private final String name;

    public static RoleEnum getByName(String name) {
        for(RoleEnum role: RoleEnum.values()) {
            if(StrUtil.equals(name, role.getName())) {
                return role;
            }
        }
        throw new IllegalArgumentException("RoleEnum: no role name :" + name);
    }

}