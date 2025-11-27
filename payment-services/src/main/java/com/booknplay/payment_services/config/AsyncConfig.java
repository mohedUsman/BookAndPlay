package com.booknplay.payment_services.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync; // NEW
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor; // NEW

import java.util.concurrent.Executor;

@Configuration
@EnableAsync // NEW: Enables @Async in this service
public class AsyncConfig {

    @Bean(name = "paymentAsyncExecutor") // NEW: Named executor for payment-related async tasks
    public Executor paymentAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4); // NEW
        executor.setMaxPoolSize(8);  // NEW
        executor.setQueueCapacity(100); // NEW
        executor.setThreadNamePrefix("payment-async-"); // NEW
        executor.initialize();
        return executor;
    }
}
