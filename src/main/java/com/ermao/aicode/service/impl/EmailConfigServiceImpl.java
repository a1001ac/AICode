package com.ermao.aicode.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ermao.aicode.common.ErrorCode;
import com.ermao.aicode.exception.ThrowUtils;
import com.ermao.aicode.mapper.EmailConfigMapper;
import com.ermao.aicode.model.dto.email.EmailConfigUpdateRequest;
import com.ermao.aicode.model.entity.AiModelConfig;
import com.ermao.aicode.model.entity.EmailConfig;
import com.ermao.aicode.service.EmailConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author 21195
 */
@Service
@Slf4j
public class EmailConfigServiceImpl extends ServiceImpl<EmailConfigMapper, EmailConfig>
        implements EmailConfigService {


    @Override
    public EmailConfig getConfig() {
        return this.getOne(new QueryWrapper<EmailConfig>().eq("id", "2004541730207023106"));
    }

    @Override
    public boolean updateConfig(EmailConfigUpdateRequest emailConfigUpdateRequest) {
        ThrowUtils.throwIf(emailConfigUpdateRequest == null, ErrorCode.PARAMS_ERROR, "请求参数错误");

        LambdaUpdateWrapper<EmailConfig> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(EmailConfig::getId, "2004541730207023106");
        updateWrapper.set(StrUtil.isNotBlank(emailConfigUpdateRequest.getHost()), EmailConfig::getHost, emailConfigUpdateRequest.getHost());
        updateWrapper.set(ObjUtil.isNotNull(emailConfigUpdateRequest.getPort()), EmailConfig::getPort, emailConfigUpdateRequest.getPort());
        updateWrapper.set(StrUtil.isNotBlank(emailConfigUpdateRequest.getUser()), EmailConfig::getUser, emailConfigUpdateRequest.getUser());
        updateWrapper.set(StrUtil.isNotBlank(emailConfigUpdateRequest.getPass()), EmailConfig::getPass, emailConfigUpdateRequest.getPass());
        updateWrapper.set(StrUtil.isNotBlank(emailConfigUpdateRequest.getFromEmail()), EmailConfig::getFromEmail, emailConfigUpdateRequest.getFromEmail());
        updateWrapper.set(ObjUtil.isNotNull(emailConfigUpdateRequest.getSslEnable()), EmailConfig::getSslEnable, emailConfigUpdateRequest.getSslEnable());

        return this.update(updateWrapper);
    }
}