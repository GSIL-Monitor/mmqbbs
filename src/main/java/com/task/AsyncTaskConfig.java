package com.task;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync // 利用@EnableAsync注解开启异步任务支持
public class AsyncTaskConfig {

    private int corePoolSize = 2;//线程池维护线程的最少数量

    private int maxPoolSize = 2;//线程池维护线程的最大数量

    private int queueCapacity = 2; //缓存队列

    private int keepAlive = 0;//允许的空闲时间

    @Bean
    public Executor myExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("mmqbbExecutor-");
        executor.setKeepAliveSeconds(keepAlive);
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); //对拒绝task的处理策略
        executor.initialize();

        System.out.println("创建线程池");

        return executor;
    }
}