package com.github.ridicuturing.guard.common.event;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class CommandFinishEvent extends ApplicationEvent {

    private Object result;

    private Object chatMessage;

    public CommandFinishEvent(Object source, Object result, Object chatMessage) {
        super(source);
        this.result = result;
        this.chatMessage = chatMessage;
    }
}
