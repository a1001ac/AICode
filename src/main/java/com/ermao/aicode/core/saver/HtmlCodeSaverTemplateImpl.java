package com.ermao.aicode.core.saver;

import cn.hutool.core.util.StrUtil;
import com.ermao.aicode.ai.model.HtmlCodeResult;
import com.ermao.aicode.common.ErrorCode;
import com.ermao.aicode.exception.BusinessException;
import com.ermao.aicode.model.enums.CodeGenTypeEnum;

/**
 * @author 21195
 */
public class HtmlCodeSaverTemplateImpl extends CodeFileSaverTemplate<HtmlCodeResult>{
    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        // 保存 HTML 文件
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        // HTML 代码不能为空
        if (StrUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML代码内容不能为空");
        }
    }
}
