package com.ermao.aicode.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    @Override
    public void addView() {
        String today = DateUtil.formatDate(new Date());
        String key = VIEW_KEY_PREFIX + today;
        // Redis 自增， key 默认保存 30 天，避免垃圾数据
        stringRedisTemplate.opsForValue().increment(key);
        log.info("访问量自增，目前访问量 key={} ", key);
        stringRedisTemplate.expire(key, 30, TimeUnit.DAYS);
    }

    @Override
    public List<View> getRecentSevenDaysViews() {
        // 获取最近 7 天的数据，按日期升序排列
        return this.query()
                .orderByDesc("dateStr")
                .last("limit 7")
                .list();
    }

    /**
     * 定时任务：每 5 分钟将 Redis 中的访问量同步到数据库
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void syncViewCountToDB() {
        String today = DateUtil.formatDate(new Date());
        String key = VIEW_KEY_PREFIX + today;

        String viewCountStr = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(viewCountStr)) {
            return;
        }

        Long viewNum = Long.parseLong(viewCountStr);

        // 查询数据库中是否已有当天的记录
        QueryWrapper<View> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dateStr", today);
        View view = this.getOne(queryWrapper);

        if (view == null) {
            // 新增
            view = new View();
            view.setDateStr(today);
            view.setViewNum(viewNum);
            this.save(view);
        } else {
            // 更新：只有当 Redis 数据大于数据库时才更新（防止数据回退）
            if (viewNum > view.getViewNum()) {
                view.setViewNum(viewNum);
                this.updateById(view);
            }
        }
        log.info("同步每日访问量成功，日期：{}，数量：{}", today, viewNum);
    }
}
