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
public class StreamingChatModelConfig {

    @Resource
    private AiModelConfigService aiModelConfigService;

    //使用多例模式，每次请求都获取最新的DB配置
    @Bean
    @Scope("prototype")
    public StreamingChatModel streamingChatModelPrototype() {
        AiModelConfig config = aiModelConfigService.getConfigByConfigKey(AiModelConfig.KEY_STREAMING);
        if (config == null) {
            throw new RuntimeException("Streaming Chat Model config not found in database.");
        }

        return OpenAiStreamingChatModel.builder()
                .apiKey(config.getApiKey())
                .baseUrl(config.getBaseUrl())
                .modelName(config.getModelName())
                .maxTokens(config.getMaxTokens())
            /*    .temperature(config.getTemperature())*/
                .logRequests(false)
                .logResponses(false)
                .build();
    }
}
