package com.booknplay.booking_services.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// NEW: Enable Async processing
import org.springframework.scheduling.annotation.EnableAsync;
// NEW: Executor for async tasks
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync // NEW: Enables @Async methods in this service
public class AsyncConfig {

    @Bean(name = "bookingAsyncExecutor") // NEW: Named executor for booking-related async tasks
    public Executor bookingAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4); // NEW: small, bounded pool
        executor.setMaxPoolSize(8);  // NEW
        executor.setQueueCapacity(100); // NEW
        executor.setThreadNamePrefix("booking-async-"); // NEW
        executor.initialize();
        return executor;
    }
}