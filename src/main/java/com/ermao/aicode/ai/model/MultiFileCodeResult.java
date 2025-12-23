package com.ermao.aicode.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * @author 21195
 */
@Data
public class MultiFileCodeResult {
    @Description("生成html代码结果")
    private String htmlCode;

    @Description("css代码")
    private String cssCode;

    @Description("js代码")
    private String jsCode;

    @Description("生成代码的描述")
    private String description;
}
