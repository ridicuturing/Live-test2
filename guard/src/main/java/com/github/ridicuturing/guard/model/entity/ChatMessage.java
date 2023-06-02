package com.github.ridicuturing.guard.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("chat_message")
@Builder
public class ChatMessage {

    @Id
    private Long id;

    private String messageSn;

    private String userSn;

    private String chatSn;

    private String role;

    private Integer type;

    private String content;

    private Integer tokens;

    private String actionType;

    @CreatedDate
    private LocalDateTime createTime;

    @LastModifiedDate
    private LocalDateTime updateTime;

}
