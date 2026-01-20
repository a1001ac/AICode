package com.ermao.aicode.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.ermao.aicode.common.BaseResponse;
import com.ermao.aicode.common.Result;
import com.ermao.aicode.constant.UserConstant;
import com.ermao.aicode.mapper.ViewMapper;
import com.ermao.aicode.model.entity.View;
import com.ermao.aicode.service.ViewService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 21195
 */
@RestController
@RequestMapping("/view")
@SaCheckRole(UserConstant.ADMIN_ROLE)
public class ViewController {

    @Resource
    private ViewService viewService;

    /**
     * 获取最近七天的系统访问量统计
     *
     * @return 统计数据列表
     */
    @GetMapping("/trend")
    public BaseResponse<List<View>> getViewTrend() {
        return Result.success(viewService.getRecentSevenDaysViews());
    }

    /**
     * 获取系统累计总访问量
     *
     * @return 总访问量
     */
    @GetMapping("/total")
    public BaseResponse<Long> getTotalViews() {
        return Result.success(viewService.getTotalViews());
    }
}
