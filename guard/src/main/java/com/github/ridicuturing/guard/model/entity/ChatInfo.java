package com.github.ridicuturing.guard.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("chat_info")
@Builder
public class ChatInfo {

    @Id
    private Long id;

    private String chatSn;

    private String chatName;

    private Long parentChatId;

    private String idTreePath;

    private String systemPrompt;

    private String userSn;

    @CreatedDate
    private LocalDateTime createTime;

    @LastModifiedDate
    private LocalDateTime updateTime;

}
