package com.ermao.aicode.ai.model;

import com.ermao.aicode.model.enums.CodeGenTypeEnum;
import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * @author 21195
 */
@Data
@Description("AI代码生成类型路由结果")
public class AiCodeGenTypeRoutingResult {

    @Description("根据用户消息识别出的代码生成类型")
    private CodeGenTypeEnum codeGenType;

    @Description("根据用户消息生成的初始标题")
    private String initialTitle;
}
