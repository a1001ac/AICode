package com.ermao.aicode.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.ermao.aicode.common.BaseResponse;
import com.ermao.aicode.common.Result;
import com.ermao.aicode.constant.UserConstant;
import com.ermao.aicode.model.entity.AiModelConfig;
import com.ermao.aicode.service.AiModelConfigService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * AI 模型配置管理
 * @author 21195
 */
@RestController
@RequestMapping("/ai")
@SaCheckRole(UserConstant.ADMIN_ROLE)
public class AiModelConfigController {

    @Resource
    private AiModelConfigService aiModelConfigService;

    // 更新配置
    @PostMapping("/update")
    public BaseResponse<Boolean> updateAiModelConfig(@RequestBody AiModelConfig config) {
        return Result.success(aiModelConfigService.updateConfigByConfigKey(config));
    }

    // 根据Key获取配置
    @GetMapping("/get")
    public BaseResponse<AiModelConfig> getAiModelConfig(@RequestParam String configKey) {
        return Result.success(aiModelConfigService.getConfigByConfigKey(configKey));
    }

}
