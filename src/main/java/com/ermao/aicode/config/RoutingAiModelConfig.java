package com.ermao.aicode.config;

import com.ermao.aicode.model.entity.AiModelConfig;
import com.ermao.aicode.service.AiModelConfigService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author 21195
 */
@Configuration
@Data
public class RoutingAiModelConfig {

    @Resource
    private AiModelConfigService aiModelConfigService;

    /**
     * 创建用于路由判断的ChatModel
     * 使用多例模式
     */
    @Bean
    @Scope("prototype")
    public ChatModel routingChatModelPrototype() {
        AiModelConfig config = aiModelConfigService.getConfigByConfigKey(AiModelConfig.KEY_ROUTING);
        if (config == null) {
            throw new RuntimeException("Routing Chat Model config not found in database.");
        }

        return OpenAiChatModel.builder()
                .apiKey(config.getApiKey())
                .modelName(config.getModelName())
                .baseUrl(config.getBaseUrl())
                .maxTokens(config.getMaxTokens())
                /*.temperature(config.getTemperature())*/
                .logRequests(false)
                .logResponses(false)
                // 数据库中如果为null则默认为1
                .maxRetries(config.getMaxRetries() != null ? config.getMaxRetries() : 1)
                .build();
    }
}
