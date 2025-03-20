package com.doc.format.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2025/3/20 17:30
 */
@Configuration
public class AppConfig {
    @Bean
    public ExecutorService executorService() {
        // 核心线程数（常驻线程）：10 → 可根据 CPU 核心数 * 2 + 1 调整
        // 最大线程数：15 → 根据业务峰值需求调整
        // 队列容量：500 → 控制任务堆积上限
        // 拒绝策略：CallerRunsPolicy → 提交者线程处理任务（避免新任务被直接拒绝）
        return new ThreadPoolExecutor(
                10,                  // 核心线程数
                15,                  // 最大线程数
                60L, TimeUnit.SECONDS, // 空闲线程存活时间（超过核心线程数的线程超时后回收）
                new ArrayBlockingQueue<>(500), // 有界队列（防止内存溢出）
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略：提交者线程处理任务
        );
    }

}
