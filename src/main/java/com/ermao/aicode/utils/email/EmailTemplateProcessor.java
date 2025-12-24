package com.ermao.aicode.utils.email;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 邮件模板处理器
 * @author ermao
 */
@Component
public interface EmailTemplateProcessor {
    /**
     * 获取模板文件名
     */
    String getTemplateName();

    /**
     * 构建模板中的动态内容
     */
    Map<String, String> buildContentMap(String code);
}
