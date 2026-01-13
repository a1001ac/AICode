package com.ermao.aicode.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ermao.aicode.mapper.ViewMapper;
import com.ermao.aicode.model.entity.View;
import com.ermao.aicode.service.ViewService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author 21195
 */
@Service
@Slf4j
public class ViewServiceImpl extends ServiceImpl<ViewMapper, View>
        implements ViewService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final String VIEW_KEY_PREFIX = "system:view:count:";
    private static final String LOCK_KEY = "system:view:sync:lock";

    /**
     * 访问量自增（高并发支持）
     */
    @Override
    public void addView() {
        String today = DateUtil.formatDate(new Date());
        String key = VIEW_KEY_PREFIX + today;

        // 原子：首次设置 + 过期时间
        Boolean first = stringRedisTemplate.opsForValue()
                .setIfAbsent(key, "1", 30, TimeUnit.DAYS);

        if (Boolean.FALSE.equals(first)) {
            stringRedisTemplate.opsForValue().increment(key);
        }

        log.info("访问量自增 key={}", key);
    }

    /**
     * 最近 7 天访问量（严格按日期）
     */
    @Override
    public List<View> getRecentSevenDaysViews() {
        String startDate = DateUtil.formatDate(DateUtil.offsetDay(new Date(), -6));

        return this.lambdaQuery()
                .ge(View::getDateStr, startDate)
                .orderByDesc(View::getDateStr)
                .list();
    }

    /**
     * 定时任务：同步 Redis → MySQL
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void syncViewCountToDB() {
        // 1. 加锁
        Boolean locked = stringRedisTemplate.opsForValue()
                .setIfAbsent(LOCK_KEY, "1", 1, TimeUnit.MINUTES);
        if (Boolean.FALSE.equals(locked)) {
            return;
        }

        try {
            // 2. 只同步今天和昨天的数据（防止0点交界处数据遗漏）
            String today = DateUtil.formatDate(new Date());
            String yesterday = DateUtil.formatDate(DateUtil.offsetDay(new Date(), -1));

            // 手动构建 Key
            syncSingleKey(VIEW_KEY_PREFIX + today);
            syncSingleKey(VIEW_KEY_PREFIX + yesterday);

        } catch (Exception e) {
            log.error("同步访问量失败", e);
        } finally {
            stringRedisTemplate.delete(LOCK_KEY);
        }
    }

    /**
     * 同步单个 Redis key
     */
    private void syncSingleKey(String key) {
        String dateStr = key.replace(VIEW_KEY_PREFIX, "");
        String value = stringRedisTemplate.opsForValue().get(key);

        if (StrUtil.isBlank(value)) {
            return;
        }

        Long viewNum;
        try {
            viewNum = Long.parseLong(value);
        } catch (NumberFormatException e) {
            log.error("访问量解析失败 key={}, value={}", key, value);
            return;
        }

        // 数据库查询（dateStr 必须唯一索引）
        View view = this.lambdaQuery()
                .eq(View::getDateStr, dateStr)
                .one();

        if (view == null) {
            view = new View();
            view.setDateStr(dateStr);
            view.setViewNum(viewNum);
            this.save(view);
        } else if (viewNum > view.getViewNum()) {
            view.setViewNum(viewNum);
            this.updateById(view);
        }

        log.info("同步访问量成功 date={}, count={}", dateStr, viewNum);
    }
}
