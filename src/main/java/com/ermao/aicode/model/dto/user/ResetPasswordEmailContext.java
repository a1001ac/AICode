package com.ermao.aicode.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 重置密码邮件上下文
 * @author ermao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordEmailContext {
    /**
     * 验证码
     */
    private String code;

    /**
     * 过期时间描述
     */
    private String expirationTime;

    /**
     * "立即前往"按钮的URL
     */
    private String toUrl;
}

