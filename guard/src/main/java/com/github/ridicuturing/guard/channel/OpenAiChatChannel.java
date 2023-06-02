package com.github.ridicuturing.guard.channel;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.github.ridicuturing.guard.channel.ai.AiService;
import com.github.ridicuturing.guard.channel.ai.entity.OpenAIChatRequest;
import com.github.ridicuturing.guard.channel.ai.entity.OpenAIResponse;
import com.github.ridicuturing.guard.channel.common.ApiKeyPoolComponent;
import com.github.ridicuturing.guard.constant.enums.MessageRole;
import com.github.ridicuturing.guard.constant.enums.RoleEnum;
import com.github.ridicuturing.guard.manager.WebClientManager;
import com.github.ridicuturing.guard.model.AiChannelContext;
import com.github.ridicuturing.guard.model.dto.AiResponseDto;
import com.github.ridicuturing.guard.model.entity.ChatMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Service
@Slf4j
public class OpenAiChatChannel extends AbstractOpenAiChannel {


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Autowired
    private WebClientManager webClientManager;


    public String getUrl() {
        return "https://api.openai.com/v1/chat/completions";
    }

    @SneakyThrows
    public Flux<AiResponseDto> doAsk(AiChannelContext aiChannelContext) {
        OpenAIChatRequest request = getRequestBody(aiChannelContext);
        String body = OBJECT_MAPPER.writeValueAsString(request);
        final String token = getToken();
        log.info("ai request body: {}", body);
        return webClientManager.create()
                .post()
                .uri(getUrl())
                .headers(headers -> headers.setBearerAuth(token))
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .onStatus(HttpStatus::isError,
                        clientResponse -> clientResponse.toEntity(String.class)
                                .doOnNext(r -> log.warn("ai http error, Response code:{}, body: {}, token:{}", r.getStatusCode(), r.getBody(), token))
                                .flatMap(responseBody -> Mono.error( new RuntimeException(responseBody.getBody()))))
                .bodyToFlux(String.class)
                .doOnNext(s -> log.trace("ai response:{}", s))
                .mapNotNull(s -> {
                    try {
                        return "[DONE]".equals(s) ? null : OBJECT_MAPPER.readValue(s, OpenAIResponse.class);
                    } catch (Exception e) {
                        log.error("ai response json error:", e);
                        throw new RuntimeException(e);
                    }
                })
                .mapNotNull(this::processResponse)
                ;
    }

    private OpenAIChatRequest getRequestBody(AiChannelContext aiChannelContext) {
        OpenAIChatRequest request = new OpenAIChatRequest();
        List<OpenAIChatRequest.Message> m;
        if (StringUtils.hasText(aiChannelContext.getSystemPrompt())) {
            m = new ArrayList<>(aiChannelContext.getMessageList().size() + 1);
            m.add(OpenAIChatRequest.Message.builder()
                    .role(RoleEnum.SYSTEM.getName())
                    .content(aiChannelContext.getSystemPrompt())
                    .build()
            );
        } else {
            m = new ArrayList<>(aiChannelContext.getMessageList().size());
        }
        aiChannelContext.getMessageList()
                .stream()
                .map(e -> BeanUtil.copyProperties(e, OpenAIChatRequest.Message.class))
                .peek(e -> e.setRole(MessageRole.OpenAiRoleEnum.getByLocalRoleEnum(MessageRole.LocalRoleEnum.valueOf(e.getRole())).getName()))
                .forEach(m::add);
        request.setMessages(m);
        request.setModel(aiChannelContext.getModel());
        request.setStream(aiChannelContext.isStream());
        request.setTemperature(0.7F);
        //request.setMaxTokens(getTokenLimit());
        return request;
    }

    private AiResponseDto processResponse(OpenAIResponse response) {
        AiResponseDto.AiResponseDtoBuilder builder = AiResponseDto.builder();
        if (CollUtil.isEmpty(response.getChoices()) || (response.getChoices().get(0).getMessage() == null && response.getChoices().get(0).getDelta() == null && response.getChoices().get(0).getDelta().getContent() == null)) {
            return null;
        }

        switch (response.getObject()) {
            case "chat.completion.chunk" -> {
                if (CollUtil.isEmpty(response.getChoices()) || response.getChoices().get(0).getDelta() == null || response.getChoices().get(0).getDelta().getContent() == null) {
                    return null;
                }
                OpenAIResponse.Message message = response.getChoices().get(0).getDelta();
                builder.content(message.getContent())
                        .stream(true)
                        .tokens(1);
            }
            case "chat.completion" -> {
                OpenAIResponse.Message message = response.getChoices().get(0).getMessage();
                builder.content(message.getContent())
                        .stream(false)
                        .tokens(response.getUsage().getCompletionTokens());
            }
        }
        return builder.build();
    }

    private Integer getTokenLimit() {
        return 4000;
    }

    private OpenAIChatRequest.Message transferMessage(ChatMessage messageDO) {
        return BeanUtil.copyProperties(messageDO, OpenAIChatRequest.Message.class);
    }

    public static void main(String[] args) {
        String content = """
                以下是一个简单的Groovy脚本，它返回字符串“hello”：
                                
                ```
                String message = \\"hello\\"
                return message
                ```\s
                                
                你可以这样运行这个脚本：
                                
                ```
                groovy myscript.groovy
                ```
                                
                脚本会打印出“hello”字符串。
                """;
        String reg = "\n```(.|\n)*?\n```";
        while (true) {
            List<String> all = ReUtil.findAll(reg, content, 0);
            System.out.println(all);
        }
    }

}
