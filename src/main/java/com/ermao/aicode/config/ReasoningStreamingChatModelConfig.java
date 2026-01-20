package com.ermao.aicode.config;

import com.ermao.aicode.model.entity.AiModelConfig;
import com.ermao.aicode.service.AiModelConfigService;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
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
public class ReasoningStreamingChatModelConfig {

    @Resource
    private AiModelConfigService aiModelConfigService;

    @Bean
    @Scope("prototype")
    public StreamingChatModel reasoningStreamingChatModelPrototype() {
        AiModelConfig config = aiModelConfigService.getConfigByConfigKey(AiModelConfig.KEY_REASONING);
        if (config == null) {
            throw new RuntimeException("Reasoning Chat Model config not found in database.");
        }

        return OpenAiStreamingChatModel.builder()
                .apiKey(config.getApiKey())
                .baseUrl(config.getBaseUrl())
                .modelName(config.getModelName())
                .maxTokens(config.getMaxTokens())
                .temperature(config.getTemperature())
                .logRequests(false)
                .logResponses(false)
                .build();
    }
}
