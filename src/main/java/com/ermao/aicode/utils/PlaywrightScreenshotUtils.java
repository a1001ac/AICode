package com.ermao.aicode.utils;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.ermao.aicode.common.ErrorCode;
import com.ermao.aicode.exception.BusinessException;
import com.ermao.aicode.exception.ThrowUtils;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

import static com.ermao.aicode.constant.AppConstant.SCREENSHOT_ROOT_DIR;

/**
 * @author 21195
 */
@Slf4j
public class PlaywrightScreenshotUtils {

    // Playwright 和 Browser 保持全局单例，复用浏览器进程
    private static final Playwright PLAYWRIGHT;
    private static final Browser BROWSER;

    static {
        try {
            log.info("正在初始化 Playwright...");
            PLAYWRIGHT = Playwright.create();
            // 启动参数优化
            BROWSER = PLAYWRIGHT.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setArgs(Arrays.asList(
                            "--no-sandbox",
                            "--disable-gpu",
                            "--disable-dev-shm-usage" // Docker 环境防崩溃关键参数
                    ))
            );
            log.info("Playwright 浏览器初始化成功");
        } catch (Exception e) {
            log.error("Playwright 初始化失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "封面图生成工具初始化失败");
        }
    }

    /**
     * 生成并保存网页封面图
     */
    public static String saveWebPageScreenshot(String webUrl) {
        ThrowUtils.throwIf(StrUtil.isBlank(webUrl), ErrorCode.PARAMS_ERROR, "应用 URL 为空");

        // 临时文件路径准备
        String rootPath = SCREENSHOT_ROOT_DIR + UUID.randomUUID().toString().substring(0, 8);
        FileUtil.mkdir(rootPath);
        String imagePath = rootPath + File.separator + RandomUtil.randomNumbers(5) + ".png";
        String compressImagePath = rootPath + File.separator + RandomUtil.randomNumbers(5) + "_compressed.jpg";

        // BrowserContext 相当于浏览器的“隐身模式窗口”，互不干扰，包含 Cookies 和缓存隔离
        try (BrowserContext context = BROWSER.newContext(new Browser.NewContextOptions()
                .setViewportSize(1600, 900)); // 设置视口大小
             Page page = context.newPage()) { // 创建属于当前请求的独立 Page

            // 设置超时，防止卡死
            page.setDefaultTimeout(30000);

            // 访问
            page.navigate(webUrl);
            // 等待策略：NETWORKIDLE (网络空闲) 适合 SPA，如果页面有轮询请求可能会超时，可视情况改为 DOMCONTENTLOADED
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // 截图
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(imagePath)));
            log.info("原始截图保存成功：{}", imagePath);

            // 压缩
            ImgUtil.compress(FileUtil.file(imagePath), FileUtil.file(compressImagePath), 0.7f);

            // 清理原始图
            FileUtil.del(imagePath);

            return compressImagePath;

        } catch (Exception e) {
            log.error("封面图生成失败：url={}", webUrl, e);
            FileUtil.del(rootPath); // 发生异常清理整个临时目录
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "网页截图失败");
        }
    }

    /**
     * 只有在 Spring 容器销毁（停机）时才关闭 Browser
     */
    @PreDestroy
    public void destroy() {
        if (BROWSER != null) {
            BROWSER.close();
        }
        PLAYWRIGHT.close();
        log.info("Playwright 资源已释放");
    }
}