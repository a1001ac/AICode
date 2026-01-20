package com.ermao.aicode.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.ermao.aicode.common.BaseResponse;
import com.ermao.aicode.common.Result;
import com.ermao.aicode.constant.UserConstant;
import com.ermao.aicode.model.dto.email.EmailConfigUpdateRequest;
import com.ermao.aicode.model.entity.EmailConfig;
import com.ermao.aicode.service.EmailConfigService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 邮件配置管理
 * @author 21195
 */
@RestController
@RequestMapping("/email")
@SaCheckRole(UserConstant.ADMIN_ROLE)
public class EmailConfigController {

    @Resource
    private EmailConfigService emailConfigService;

    // 更新配置
    @PostMapping("/update")
    public BaseResponse<Boolean> updateEmailConfig(@RequestBody EmailConfigUpdateRequest emailConfigUpdateRequest) {
        return Result.success(emailConfigService.updateConfig(emailConfigUpdateRequest));
    }

    // 根据Key获取配置
    @GetMapping("/get")
    public BaseResponse<EmailConfig> getEmailConfig() {
        return Result.success(emailConfigService.getConfig());
    }

}
