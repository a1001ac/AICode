package com.ermao.aicode.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ermao.aicode.mapper.ViewMapper;
import com.ermao.aicode.model.entity.View;
import com.ermao.aicode.service.ViewService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private static final String TOTAL_VIEW_KEY = "system:view:total";
    private static final String LOCK_KEY = "system:view:sync:lock";

    /**
     * 系统启动初始化
     */
    @PostConstruct
    public void init() {
        // 如果 Redis 中没有总访问量 key，则初始化为 3868，永不过期
        stringRedisTemplate.opsForValue().setIfAbsent(TOTAL_VIEW_KEY, "3868");
    }

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

        // 总访问量自增
        stringRedisTemplate.opsForValue().increment(TOTAL_VIEW_KEY);

        log.info("访问量自增 key={}", key);
    }

    /**
     * 获取系统总访问量
     * 优先从 Redis 获取，失败则统计数据库
     */
    @Override
    public Long getTotalViews() {
        String val = stringRedisTemplate.opsForValue().get(TOTAL_VIEW_KEY);
        if (StrUtil.isNotBlank(val)) {
            try {
                return Long.parseLong(val);
            } catch (NumberFormatException e) {
                log.error("总访问量解析失败", e);
            }
        }

        // 降级：统计数据库所有记录的 viewNum 之和
        try {
            return this.list().stream()
                    .mapToLong(View::getViewNum)
                    .sum();
        } catch (Exception e) {
            log.error("数据库统计总访问量失败", e);
            return 0L;
        }
    }

    /**
     * 最近 7 天访问量（严格按日期）
     * 优先从 Redis 获取，失败则降级查询数据库
     */
    @Override
    public List<View> getRecentSevenDaysViews() {
        // 1. 尝试从 Redis 获取
        try {
            List<String> dateStrList = new ArrayList<>();
            List<String> keys = new ArrayList<>();
            // 构造最近 7 天的 key（倒序：今天 -> 6天前）
            for (int i = 0; i < 7; i++) {
                String dateStr = DateUtil.formatDate(DateUtil.offsetDay(new Date(), -i));
                dateStrList.add(dateStr);
                keys.add(VIEW_KEY_PREFIX + dateStr);
            }

            // 批量获取值
            List<String> values = stringRedisTemplate.opsForValue().multiGet(keys);
            if (values != null && !values.isEmpty()) {
                List<View> viewList = new ArrayList<>();
                for (int i = 0; i < values.size(); i++) {
                    String val = values.get(i);
                    View view = new View();
                    view.setDateStr(dateStrList.get(i));
                    // 如果 Redis 中没有值（null 或 ""），则视为 0
                    view.setViewNum(StrUtil.isNotBlank(val) ? Long.parseLong(val) : 0L);
                    viewList.add(view);
                }
                return viewList;
            }
        } catch (Exception e) {
            log.error("从 Redis 获取最近7天访问量失败，降级为查询数据库", e);
        }

        // 2. 降级方案：查询数据库
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

            // 3. 检查总访问量 Key，确保持久化或存在
            if (!stringRedisTemplate.hasKey(TOTAL_VIEW_KEY)) {
                Long dbTotal = this.list().stream().mapToLong(View::getViewNum).sum();
                stringRedisTemplate.opsForValue().set(TOTAL_VIEW_KEY, String.valueOf(dbTotal));
                log.warn("检测到 Redis 总访问量 Key 缺失，已从数据库恢复: {}", dbTotal);
            }

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

        long viewNum = 0L;
        if (StrUtil.isNotBlank(value)) {
            try {
                viewNum = Long.parseLong(value);
            } catch (NumberFormatException e) {
                log.error("访问量解析失败 key={}, value={}", key, value);
                return;
            }
        }

        // 数据库查询（dateStr 必须唯一索引）
        View view = this.lambdaQuery()
                .eq(View::getDateStr, dateStr)
                .one();

        if (view == null) {
            // 如果数据库没有记录，无论 viewNum 是 0 还是其他值，都插入一条新记录
            view = new View();
            view.setDateStr(dateStr);
            view.setViewNum(viewNum);
            this.save(view);
            log.info("同步访问量(新增) date={}, count={}", dateStr, viewNum);
        } else if (StrUtil.isNotBlank(value) && viewNum > view.getViewNum()) {
            // 只有当 Redis 中有值（非空）且比数据库大时才更新
            // 避免因为 Redis Key 不存在（上面的逻辑 viewNum 会是0）而覆盖了数据库已有的数据
            view.setViewNum(viewNum);
            this.updateById(view);
            log.info("同步访问量(更新) date={}, count={}", dateStr, viewNum);
        }
    }
}
