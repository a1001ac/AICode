package com.ermao.aicode.utils;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.ermao.aicode.model.entity.EmailConfig;
import com.ermao.aicode.service.EmailConfigService;
import com.ermao.aicode.utils.email.EmailTemplateProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author 21195
 */
@Component
@RequiredArgsConstructor
public class EmailUtil {

    private final List<EmailTemplateProcessor> processors;
    private final EmailConfigService emailConfigService;

    public void sendTemplateEmail(String email, Class<? extends EmailTemplateProcessor> processorClass, String code, String title) {
        // 1. 获取数据库配置
        EmailConfig emailConfig = emailConfigService.getConfig();

        if (emailConfig == null) {
            throw new RuntimeException("系统邮件服务未配置");
        }

        // 2. 构建 Hutool MailAccount 对象
        MailAccount account = new MailAccount();
        account.setHost(emailConfig.getHost());
        account.setPort(emailConfig.getPort());
        account.setAuth(true);
        account.setFrom(emailConfig.getFromEmail());
        account.setUser(emailConfig.getUser());
        account.setPass(emailConfig.getPass());
        account.setSslEnable(emailConfig.getSslEnable() == 1);

        // 3. 准备模板内容
        EmailTemplateProcessor processor = processors.stream()
                .filter(p -> processorClass.isAssignableFrom(p.getClass()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("模板处理器未注册: " + processorClass.getSimpleName()));

        String html = loadTemplate(processor.getTemplateName());
        if (html.isEmpty()) {
            return;
        }

        Map<String, String> variables = processor.buildContentMap(code);
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            html = html.replace(entry.getKey(), entry.getValue());
        }

        // 4. 发送邮件 (传入 account)
        MailUtil.send(account, email, title, html, true);
    }

    private String loadTemplate(String name) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/" + name);
            byte[] bytes = resource.getInputStream().readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
}
