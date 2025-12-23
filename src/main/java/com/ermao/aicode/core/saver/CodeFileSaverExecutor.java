package com.ermao.aicode.core.saver;

import com.ermao.aicode.ai.model.HtmlCodeResult;
import com.ermao.aicode.ai.model.MultiFileCodeResult;
import com.ermao.aicode.common.ErrorCode;
import com.ermao.aicode.exception.BusinessException;
import com.ermao.aicode.model.enums.CodeGenTypeEnum;

import java.io.File;

/**
 * 代码文件保存执行器
 * 根据代码生成类型执行相应的保存逻辑
 * @author 21195
 */
public class CodeFileSaverExecutor {

    private static final HtmlCodeSaverTemplateImpl htmlCodeFileSaver = new HtmlCodeSaverTemplateImpl();

    private static final MultiFileCodeSaverTemplateImpl multiFileCodeFileSaver = new MultiFileCodeSaverTemplateImpl();

    /**
     * 执行代码保存
     *
     * @param codeResult  代码结果对象
     * @param codeGenType 代码生成类型
     * @return 保存的目录
     */
    public static File executeSaver(Object codeResult, CodeGenTypeEnum codeGenType, Long appId) {
        return switch (codeGenType) {
            case HTML -> htmlCodeFileSaver.saveCode((HtmlCodeResult) codeResult, appId);
            case MULTI_FILE -> multiFileCodeFileSaver.saveCode((MultiFileCodeResult) codeResult, appId);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + codeGenType);
        };
    }
}
