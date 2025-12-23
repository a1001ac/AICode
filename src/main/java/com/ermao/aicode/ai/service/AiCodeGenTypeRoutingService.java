package com.ermao.aicode.ai.service;

import com.ermao.aicode.ai.model.AiCodeGenTypeRoutingResult;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * @author 21195
 */
public interface AiCodeGenTypeRoutingService {

    /**
     * 生成类型以及标题
     * @param userMessage 用户消息
     * @return
     */
    @SystemMessage(fromResource="prompt/codegen-routing-system-prompt.txt")
    AiCodeGenTypeRoutingResult aiCodeGenTypeRouting(@UserMessage String userMessage);

}
