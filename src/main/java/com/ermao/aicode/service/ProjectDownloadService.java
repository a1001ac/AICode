package com.ermao.aicode.service;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author 21195
 */
public interface ProjectDownloadService {

    /**
     * 下载项目压缩包
     * @return
     */
    void downloadProjectAsZip(String projectPath, String  downloadFileName, HttpServletResponse response);
}
