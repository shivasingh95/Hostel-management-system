package com.hostel.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Defines the thread pool used by @Async methods (see
 * NotificationService). Demonstrates the multithreading unit of the
 * syllabus applied through Spring's managed executor rather than raw
 * Thread objects — the concept (concurrent task execution, thread
 * lifecycle, pool sizing) is the same, just production-grade.
 */
@Configuration
public class AsyncConfig {

    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Notify-");
        executor.initialize();
        return executor;
    }
}
