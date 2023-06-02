package com.github.ridicuturing.guard.channel;

import cn.hutool.extra.spring.SpringUtil;
import com.github.ridicuturing.guard.channel.common.ApiKeyPoolComponent;

public class AbstractOpenAiChannel {

    protected String getToken() {
        return SpringUtil.getBean(ApiKeyPoolComponent.class).getOpenAiKey();
    }

}
