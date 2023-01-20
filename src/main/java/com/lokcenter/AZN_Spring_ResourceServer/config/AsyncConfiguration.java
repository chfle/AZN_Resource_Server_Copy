package com.lokcenter.AZN_Spring_ResourceServer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * (1 + 50 / 10));
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * (1 + 50 / 10));
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("serviceThread_");
        executor.initialize();

        return executor;
    }
}
