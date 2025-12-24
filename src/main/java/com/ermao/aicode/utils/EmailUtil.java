package com.ermao.aicode.utils;

import cn.hutool.extra.mail.MailUtil;
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

    public void sendTemplateEmail(String email, Class<? extends EmailTemplateProcessor> processorClass, String code, String title) {
        EmailTemplateProcessor processor = processors.stream()
                .filter(p -> processorClass.isAssignableFrom(p.getClass()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("模板处理器未注册: " + processorClass.getSimpleName()));

        String html = loadTemplate(processor.getTemplateName());
        if (html.isEmpty()) return;

        Map<String, String> variables = processor.buildContentMap(code);
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            html = html.replace(entry.getKey(), entry.getValue());
        }

        MailUtil.send(email, title, html, true);
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

