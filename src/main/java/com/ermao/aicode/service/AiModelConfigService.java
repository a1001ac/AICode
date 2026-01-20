package com.ermao.aicode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ermao.aicode.model.entity.AiModelConfig;

/**
 * @author 21195
 */
public interface AiModelConfigService extends IService<AiModelConfig> {

    AiModelConfig getConfigByConfigKey(String configKey);

    boolean updateConfigByConfigKey(AiModelConfig config);
}

