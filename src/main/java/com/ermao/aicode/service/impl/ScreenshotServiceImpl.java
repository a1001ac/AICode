package com.ermao.aicode.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.ermao.aicode.common.ErrorCode;
import com.ermao.aicode.exception.ThrowUtils;
import com.ermao.aicode.service.ScreenshotService;
import com.ermao.aicode.utils.PlaywrightScreenshotUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author 21195
 */
@Service
@Slf4j
public class ScreenshotServiceImpl implements ScreenshotService {

    @Resource
    private FileStorageService fileStorageService;

    @Override
    public String generateAndUploadScreenshot(String webUrl) {
        ThrowUtils.throwIf(StrUtil.isBlank(webUrl), ErrorCode.PARAMS_ERROR, "网页URL不能为空");
        log.info("开始生成网页截图，URL: {}", webUrl);
        // 1. 生成本地截图
        String localScreenshotPath = PlaywrightScreenshotUtils.saveWebPageScreenshot(webUrl);
        ThrowUtils.throwIf(StrUtil.isBlank(localScreenshotPath), ErrorCode.OPERATION_ERROR, "本地截图生成失败");
        try {
            // 2. 上传到对象存储
            File screenshotFile = new File(localScreenshotPath);
            // 使用 x-file-storage 上传文件
            FileInfo fileInfo = fileStorageService.of(screenshotFile)
                    .setPath("screenshots/") // 指定上传目录
                    .upload();
            String url = fileInfo.getUrl();
            ThrowUtils.throwIf(StrUtil.isBlank(url), ErrorCode.OPERATION_ERROR, "截图上传对象存储失败");
            log.info("网页截图生成并上传成功: {} -> {}", webUrl, url);
            return url;
        } finally {
            // 3. 清理本地文件
            cleanupLocalFile(localScreenshotPath);
        }
    }


    /**
     * 清理本地文件
     *
     * @param localFilePath 本地文件路径
     */
    private void cleanupLocalFile(String localFilePath) {
        if (StrUtil.isBlank(localFilePath)) {
            return;
        }
        File localFile = new File(localFilePath);
        if (localFile.exists()) {
            File parentDir = localFile.getParentFile();
            if (parentDir != null && parentDir.exists()) {
                FileUtil.del(parentDir);
                log.info("本地截图目录已清理: {}", parentDir.getAbsolutePath());
            }
        }
    }
}
