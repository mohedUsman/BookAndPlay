package com.booknplay.booking_services.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// NEW: Enable Async processing
import org.springframework.scheduling.annotation.EnableAsync;
// NEW: Executor for async tasks
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "bookingAsyncExecutor")
    public Executor bookingAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("booking-async-");
        executor.initialize();
        return executor;
    }
}