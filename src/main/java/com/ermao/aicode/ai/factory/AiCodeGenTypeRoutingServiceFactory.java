package com.ermao.aicode.ai.factory;

import com.ermao.aicode.ai.service.AiCodeGenTypeRoutingService;
import com.ermao.aicode.utils.SpringContextUtil;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 21195
 */
@Configuration
@Slf4j
public class AiCodeGenTypeRoutingServiceFactory {

    public AiCodeGenTypeRoutingService createAiCodeGenTypeRoutingService(){
        ChatModel chatModel = SpringContextUtil.getBean("routingChatModelPrototype", ChatModel.class);
        return AiServices.builder(AiCodeGenTypeRoutingService.class)
                .chatModel(chatModel)
                .build();
    }

    @Bean
    public AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService(){
        return createAiCodeGenTypeRoutingService();
    }

}
