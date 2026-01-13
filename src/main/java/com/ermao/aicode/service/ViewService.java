package com.ermao.aicode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ermao.aicode.model.entity.View;

import java.util.List;

/**
 * @author 21195
 */
public interface ViewService extends IService<View> {

    /**
     * 记录一次访问（Redis + 定时同步）
     */
    void addView();

    /**
     * 获取最近七天的访问量统计
     * @return 统计列表
     */
    List<View> getRecentSevenDaysViews();
}
