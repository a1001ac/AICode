package com.ermao.aicode.utils.email;

import com.ermao.aicode.model.dto.user.ResetPasswordEmailContext;
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
    public Map<String, String> buildContentMap(Object context) {
        if (!(context instanceof ResetPasswordEmailContext emailContext)) {
            return new HashMap<>();
        }
        Map<String, String> map = new HashMap<>();
        map.put("${code}", emailContext.getCode());
        map.put("${expirationTime}", emailContext.getExpirationTime());
        map.put("${toUrl}", emailContext.getToUrl());
        return map;
    }

}
