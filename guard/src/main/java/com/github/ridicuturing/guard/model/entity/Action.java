package com.github.ridicuturing.guard.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("action")
@Builder
public class Action {

    @Id
    private Long id;

    private String messageSn;

    private String chatSn;

    private String actionContent;

    private String result;

    private Long state;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
