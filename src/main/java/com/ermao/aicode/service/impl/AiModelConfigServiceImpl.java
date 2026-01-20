package com.ermao.aicode.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ermao.aicode.common.ErrorCode;
import com.ermao.aicode.exception.ThrowUtils;
import com.ermao.aicode.mapper.AiModelConfigMapper;
import com.ermao.aicode.model.entity.AiModelConfig;
import com.ermao.aicode.service.AiModelConfigService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author 21195
 */
@Service
public class AiModelConfigServiceImpl extends ServiceImpl<AiModelConfigMapper, AiModelConfig>
    implements AiModelConfigService {

    @Override
    public AiModelConfig getConfigByConfigKey(String configKey) {
        return this.getOne(new LambdaQueryWrapper<AiModelConfig>()
                .eq(AiModelConfig::getConfigKey, configKey));
    }

    @Override
    public boolean updateConfigByConfigKey(AiModelConfig config) {
        ThrowUtils.throwIf(config == null || !StringUtils.hasText(config.getConfigKey()), ErrorCode.PARAMS_ERROR, "请求参数错误");

        LambdaUpdateWrapper<AiModelConfig> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AiModelConfig::getConfigKey, config.getConfigKey());
        updateWrapper.set(StrUtil.isNotBlank(config.getBaseUrl()), AiModelConfig::getBaseUrl, config.getBaseUrl());
        updateWrapper.set(StrUtil.isNotBlank(config.getApiKey()), AiModelConfig::getApiKey, config.getApiKey());
        updateWrapper.set(StrUtil.isNotBlank(config.getModelName()), AiModelConfig::getModelName, config.getModelName());
        updateWrapper.set(ObjUtil.isNotNull(config.getMaxTokens()), AiModelConfig::getMaxTokens, config.getMaxTokens());
        updateWrapper.set(ObjUtil.isNotNull(config.getTemperature()), AiModelConfig::getTemperature, config.getTemperature());
        updateWrapper.set(ObjUtil.isNotNull(config.getMaxRetries()), AiModelConfig::getMaxRetries, config.getMaxRetries());

        return this.update(updateWrapper);
    }
}
