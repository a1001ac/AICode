package com.ermao.aicode.ai.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

/**
 * @author 21195
 */
public interface AiCodeGeneratorService {
    /**
     * 生成html代码
     * @return
     */
    @SystemMessage(fromResource="prompt/codegen-html-system-prompt.txt")
    Flux<String> generateHtmlCodeStream(String userMessage);

    /**
     * 生成多文件代码
     * @return
     */
    @SystemMessage(fromResource="prompt/codegen-multi-file-system-prompt.txt")
    Flux<String> generateMultiFileCodeStream(String userMessage);

    /**
     * 生成多文件（VUE工程）代码
     * @return
     */
    @SystemMessage(fromResource="prompt/codegen-vue-project-system-prompt.txt")
    TokenStream generateVueProjectCodeStream(@MemoryId long memoryId, @UserMessage String userMessage);
}
