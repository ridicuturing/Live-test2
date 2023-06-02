package com.github.ridicuturing.guard.model;

public class SecurityConstant {
    public enum RoleEnum {
        ANYONE(AuthorityEnum.CHAT),
        LOGGED(AuthorityEnum.CHAT),
        VIP(AuthorityEnum.CHAT, AuthorityEnum.EXECUTE),
        BUSINESS_MANAGER(AuthorityEnum.CHAT, AuthorityEnum.BUSINESS_MANAGEMENT),
        ADMIN(AuthorityEnum.values()),
        ;
        private final AuthorityEnum[] authorityEnums;

        RoleEnum (AuthorityEnum... authorityEnums) {
            this.authorityEnums = authorityEnums;
        }
    }
    public enum AuthorityEnum {
        CHAT,
        EXECUTE,
        BUSINESS_MANAGEMENT,
        SYSTEM_MANAGEMENT,
        ;
    }
}
