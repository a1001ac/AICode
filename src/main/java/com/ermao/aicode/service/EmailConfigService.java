package com.ermao.aicode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ermao.aicode.model.dto.email.EmailConfigUpdateRequest;
import com.ermao.aicode.model.entity.AiModelConfig;
import com.ermao.aicode.model.entity.EmailConfig;

/**
 * @author 21195
 */
public interface EmailConfigService extends IService<EmailConfig> {
    EmailConfig getConfig();

    boolean updateConfig(EmailConfigUpdateRequest emailConfigUpdateRequest);

}
