package com.ermao.aicode.utils.email;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ermao
 */
@Component
public class ResetPasswordEmailProcessor implements EmailTemplateProcessor {

    @Override
    public String getTemplateName() {
        return "reset-password-template.html";
    }

    @Override
    public Map<String, String> buildContentMap(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("${code}", String.valueOf(code));
        map.put("${expirationTime}", "5分钟");
        map.put("${toUrl}", "http://101.126.151.232");
        return map;
    }

}
